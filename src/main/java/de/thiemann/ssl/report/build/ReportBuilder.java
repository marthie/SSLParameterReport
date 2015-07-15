package de.thiemann.ssl.report.build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.thiemann.ssl.report.model.Certificate;
import de.thiemann.ssl.report.model.Report;
import de.thiemann.ssl.report.model.SSLv2Certificate;
import de.thiemann.ssl.report.model.ServerHello;
import de.thiemann.ssl.report.model.ServerHelloSSLv2;
import de.thiemann.ssl.report.util.CipherSuiteUtil;
import de.thiemann.ssl.report.util.IOUtil;
import de.thiemann.ssl.report.util.SSLVersions;

/*
 * ----------------------------------------------------------------------
 * Copyright (c) 2012  Thomas Pornin <pornin@bolet.org>
 * Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ----------------------------------------------------------------------
 */

public class ReportBuilder {

	/*
	 * A constant SSLv2 CLIENT-HELLO message. Only one connection is needed for
	 * SSLv2, since the server response will contain _all_ the cipher suites
	 * that the server is willing to support.
	 * 
	 * Note: when (mis)interpreted as a SSLv3+ record, this message apparently
	 * encodes some data of (invalid) 0x80 type, using protocol version TLS
	 * 44.1, and record length of 2 bytes. Thus, the receiving part will quickly
	 * conclude that it will not support that, instead of stalling for more data
	 * from the client.
	 */
	private static final byte[] SSL2_CLIENT_HELLO = { (byte) 0x80,
			(byte) 0x2E, // header (record length)
			(byte) 0x01, // message type (CLIENT HELLO)
			(byte) 0x00,
			(byte) 0x02, // version (0x0002)
			(byte) 0x00,
			(byte) 0x15, // cipher specs list length
			(byte) 0x00,
			(byte) 0x00, // session ID length
			(byte) 0x00,
			(byte) 0x10, // challenge length
			0x01, 0x00,
			(byte) 0x80, // SSL_CK_RC4_128_WITH_MD5
			0x02, 0x00,
			(byte) 0x80, // SSL_CK_RC4_128_EXPORT40_WITH_MD5
			0x03, 0x00,
			(byte) 0x80, // SSL_CK_RC2_128_CBC_WITH_MD5
			0x04, 0x00,
			(byte) 0x80, // SSL_CK_RC2_128_CBC_EXPORT40_WITH_MD5
			0x05, 0x00,
			(byte) 0x80, // SSL_CK_IDEA_128_CBC_WITH_MD5
			0x06, 0x00,
			(byte) 0x40, // SSL_CK_DES_64_CBC_WITH_MD5
			0x07, 0x00,
			(byte) 0xC0, // SSL_CK_DES_192_EDE3_CBC_WITH_MD5
			0x54, 0x54, 0x54,
			0x54, // challenge data (16 bytes)
			0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54, 0x54,
			0x54 };

	static final int CHANGE_CIPHER_SPEC = 20;
	static final int HANDSHAKE = 22;
	static final int APPLICATION = 23;

	private static final SecureRandom RNG = new SecureRandom();

	public List<Report> generateMultipleReport(InetAddress[] ips, int port) {
		if (ips != null && ips.length > 0) {
			List<Report> reportList = new ArrayList<Report>();
			for (InetAddress ip : ips) {
				Report r = generateReport(ip, port);

				if (r != null)
					reportList.add(r);
			}

			if (reportList.isEmpty())
				return null;

			return reportList;
		}

		return null;
	}

	public Report generateReport(InetAddress ip, int port) {
		Report report = new Report(ip, port);

		InetSocketAddress isa = new InetSocketAddress(ip, port);

		report.supportedSSLVersions = new TreeSet<Integer>();

		for (SSLVersions version : SSLVersions.values()) {

			if (version.equals(SSLVersions.SSLv2))
				continue;

			ServerHello serverHello = connect(isa, version.getIntVersion(),
					CipherSuiteUtil.CIPHER_SUITES.keySet());
			if (serverHello == null) {
				continue;
			}
			report.supportedSSLVersions.add(serverHello.protoVersion);
			report.compress = serverHello.compression;
		}

		// check support for version SSL2
		ServerHelloSSLv2 serverHelloV2 = connectV2(isa);

		if (serverHelloV2 != null) {
			report.supportedSSLVersions.add(0x0200);
		}

		// check cipher suites

		report.supportedCipherSuite = new TreeMap<Integer, Set<Integer>>();
		report.serverCert = new TreeMap<Integer, Set<Certificate>>();

		if (serverHelloV2 != null) {
			Set<Integer> supportedSSL2CipherSuites = new TreeSet<Integer>();
			for (int cipherSuiteId : serverHelloV2.cipherSuites) {
				supportedSSL2CipherSuites.add(cipherSuiteId);
			}

			report.supportedCipherSuite.put(SSLVersions.SSLv2.getIntVersion(),
					supportedSSL2CipherSuites);

			if (serverHelloV2.serverCertName != null) {
				Set<Certificate> certs = new TreeSet<Certificate>();
				SSLv2Certificate cert = new SSLv2Certificate(1,
						serverHelloV2.serverCertName,
						serverHelloV2.serverCertHash);
				certs.add(cert);

				report.serverCert.put(SSLVersions.SSLv2.getIntVersion(), certs);
			}
		}

		for (int version : report.supportedSSLVersions) {
			if (version == SSLVersions.SSLv2.getIntVersion()) {
				continue;
			}

			Set<Integer> versionSupportedCiphers = supportedSuites(isa, version);
			report.supportedCipherSuite.put(version, versionSupportedCiphers);

			Set<Certificate> versionCertificates = addCertificates(isa, version);
			report.serverCert.put(version, versionCertificates);
		}

		return report;
	}

	/*
	 * Get cipher suites supported by the server. This is done by repeatedly
	 * contacting the server, each time removing from our list of supported
	 * suites the suite which the server just selected. We keep on until the
	 * server can no longer respond to us with a ServerHello.
	 */
	public Set<Integer> supportedSuites(InetSocketAddress isa, int version) {
		Set<Integer> cs = new TreeSet<Integer>(
				CipherSuiteUtil.CIPHER_SUITES.keySet());
		Set<Integer> rs = new TreeSet<Integer>();
		for (;;) {
			ServerHello sh = connect(isa, version, cs);
			if (sh == null) {
				break;
			}
			if (!cs.contains(sh.cipherSuite)) {
				System.err.printf("[ERR: server wants to use"
						+ " cipher suite 0x%04X which client"
						+ " did not announce]", sh.cipherSuite);
				System.err.println();
				break;
			}
			cs.remove(sh.cipherSuite);
			rs.add(sh.cipherSuite);
		}
		return rs;
	}

	public Set<Certificate> addCertificates(InetSocketAddress isa, int version) {
		Set<Certificate> serverCerts = null;
		ServerHello sh = connect(isa, version,
				CipherSuiteUtil.CIPHER_SUITES.keySet());

		if (sh != null && sh.certificateChain != null) {
			serverCerts = sh.certificateChain;
		}

		return serverCerts;
	}

	/*
	 * Connect to the server, send a ClientHello, and decode the response
	 * (ServerHello). On error, null is returned.
	 */
	public ServerHello connect(InetSocketAddress isa, int version,
			Collection<Integer> cipherSuites) {

		Socket s = null;

		try {
			s = new Socket();
			try {
				s.connect(isa);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			byte[] ch = makeClientHello(version, cipherSuites);

			OutputRecord orec = new OutputRecord(s.getOutputStream());
			orec.setType(HANDSHAKE);
			orec.setVersion(version);
			orec.write(ch);
			orec.flush();

			return new ServerHello(s.getInputStream());
		} catch (IOException ioe) {
			// ignored
		} finally {
			try {
				s.close();
			} catch (IOException ioe) {
				// ignored
			}
		}
		return null;
	}

	/*
	 * Connect to the server, send a SSLv2 CLIENT HELLO, and decode the response
	 * (SERVER HELLO). On error, null is returned.
	 */
	public ServerHelloSSLv2 connectV2(InetSocketAddress isa) {
		Socket s = null;
		try {
			s = new Socket();
			try {
				s.connect(isa);
			} catch (IOException ioe) {
				System.err.println("could not connect to " + isa + ": "
						+ ioe.toString());
				return null;
			}
			s.getOutputStream().write(SSL2_CLIENT_HELLO);
			return new ServerHelloSSLv2(s.getInputStream());
		} catch (IOException ioe) {
			// ignored
		} finally {
			try {
				s.close();
			} catch (IOException ioe) {
				// ignored
			}
		}
		return null;
	}

	/*
	 * Build a ClientHello message, with the specified maximum supported
	 * version, and list of cipher suites.
	 */
	public byte[] makeClientHello(int version, Collection<Integer> cipherSuites) {
		try {
			return makeClientHello0(version, cipherSuites);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public byte[] makeClientHello0(int version, Collection<Integer> cipherSuites)
			throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();

		/*
		 * Message header: message type: one byte (1 = "ClientHello") message
		 * length: three bytes (this will be adjusted at the end of this
		 * method).
		 */
		b.write(1);
		b.write(0);
		b.write(0);
		b.write(0);

		/*
		 * The maximum version that we intend to support.
		 */
		b.write(version >>> 8);
		b.write(version);

		/*
		 * The client random has length 32 bytes, but begins with the client's
		 * notion of the current time, over 32 bits (seconds since 1970/01/01
		 * 00:00:00 UTC, not counting leap seconds).
		 */
		byte[] rand = new byte[32];
		RNG.nextBytes(rand);
		IOUtil.enc32be((int) (System.currentTimeMillis() / 1000), rand, 0);
		b.write(rand);

		/*
		 * We send an empty session ID.
		 */
		b.write(0);

		/*
		 * The list of cipher suites (list of 16-bit values; the list length in
		 * bytes is written first).
		 */
		int num = cipherSuites.size();
		byte[] cs = new byte[2 + num * 2];
		IOUtil.enc16be(num * 2, cs, 0);
		int j = 2;
		for (int s : cipherSuites) {
			IOUtil.enc16be(s, cs, j);
			j += 2;
		}
		b.write(cs);

		/*
		 * Compression methods: we claim to support Deflate (1) and the standard
		 * no-compression (0), with Deflate being preferred.
		 */
		b.write(2);
		b.write(1);
		b.write(0);

		/*
		 * If we had extensions to add, they would go here.
		 */

		/*
		 * We now get the message as a blob. The message length must be adjusted
		 * in the header.
		 */
		byte[] msg = b.toByteArray();
		IOUtil.enc24be(msg.length - 4, msg, 1);
		return msg;
	}

}
