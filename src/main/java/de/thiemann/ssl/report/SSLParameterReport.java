package de.thiemann.ssl.report;

/*
 * Command-line tool to test a SSL/TLS parameter on a server.
 * =====================================================================
 *
 * This application connects to the provided SSL/TLS server (by name and
 * port) and extracts the following information:
 * - supported versions (SSL 2.0, SSL 3.0, TLS 1.0 to 1.2)
 * - support of Deflate compression
 * - list of supported cipher suites (for each protocol version)
 *
 * ----------------------------------------------------------------------
 * Copyright (c) 2012  Thomas Pornin <pornin@bolet.org>
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SSLParameterReport {

	private static String NL = System.getProperty("line.separator");

	static void usage() {
		System.err.println("usage: SSLParameterReport servername [ port ]");
		System.exit(1);
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0 || args.length > 2) {
			usage();
		}
		String name = args[0];
		int port = 443;
		if (args.length == 2) {
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				usage();
			}
			if (port <= 0 || port > 65535) {
				usage();
			}
		}
		InetSocketAddress isa = new InetSocketAddress(name, port);

		// check support for versions SSL3, TLS1.0, TLS1.1, TLS1.2
		Set<Integer> supportedVersions = new TreeSet<Integer>();
		boolean compress = false;
		for (int v = 0x0300; v <= 0x0303; v++) {
			ServerHello serverHello = connect(isa, v,
					CipherSuiteUtil.CIPHER_SUITES.keySet());
			if (serverHello == null) {
				continue;
			}
			supportedVersions.add(serverHello.protoVersion);
			compress = serverHello.compression;
		}

		// check support for version SSL2
		ServerHelloSSLv2 serverHelloV2 = connectV2(isa);

		if (serverHelloV2 != null) {
			supportedVersions.add(0x0200);
		}

		if (supportedVersions.size() == 0) {
			System.out.println("No SSL/TLS server at " + isa);
			System.exit(1);
		}

		StringBuffer sb = new StringBuffer();

		sb.append(NL)
				.append("Report from: ")
				.append(String.format("%1$tF %1$tT", System.currentTimeMillis()));

		sb.append(NL)
				.append("--------------------------------------------------------------------------------");

		sb.append(NL).append("Supported protocol versions:");
		for (int version : supportedVersions) {
			sb.append(" ").append(versionString(version));
		}

		sb.append(NL).append("Deflate compression: ")
				.append((compress ? "YES" : "no"));

		sb.append(NL)
				.append("--------------------------------------------------------------------------------");

		sb.append(NL).append("Supported cipher suites")
				.append(" (ORDER IS NOT SIGNIFICANT):");

		Map<Integer, Set<Integer>> supportedCipherSuite = new TreeMap<Integer, Set<Integer>>();
		Set<String> certID = new TreeSet<String>();

		if (serverHelloV2 != null) {
			sb.append(NL).append("  ").append(versionString(0x0200));
			Set<Integer> vc2 = new TreeSet<Integer>();
			for (int c : serverHelloV2.cipherSuites) {
				vc2.add(c);
			}
			for (int c : vc2) {
				sb.append(NL).append("     ")
						.append(CipherSuiteUtil.cipherSuiteStringV2(c));
			}

			supportedCipherSuite.put(0x0200, vc2);

			if (serverHelloV2.serverCertName != null) {
				certID.add(serverHelloV2.serverCertHash + ": "
						+ serverHelloV2.serverCertName);
			}
		}

		for (int version : supportedVersions) {

			if (version == 0x0200) {
				continue;
			}

			Set<Integer> versionSupportedCiphers = supportedSuites(isa, version);
			supportedCipherSuite.put(version, versionSupportedCiphers);

			addCertificates(certID, isa, version);
			
			sb.append(NL).append("  ").append(versionString(version));
			for (int c : versionSupportedCiphers) {
				sb.append(NL).append("     ")
						.append(CipherSuiteUtil.cipherSuiteString(c));
			}

		}

		sb.append(NL)
				.append("--------------------------------------------------------------------------------");
		if (certID.size() == 0) {
			sb.append(NL).append("No server certificate !");
		} else {
			sb.append(NL).append("Server certificate(s):");
			for (String cc : certID) {
				sb.append(NL).append("  ").append(cc);
			}
		}

		System.out.println(sb.toString());
	}

	static final int CHANGE_CIPHER_SPEC = 20;
	static final int HANDSHAKE = 22;
	static final int APPLICATION = 23;

	/*
	 * Get cipher suites supported by the server. This is done by repeatedly
	 * contacting the server, each time removing from our list of supported
	 * suites the suite which the server just selected. We keep on until the
	 * server can no longer respond to us with a ServerHello.
	 */
	static Set<Integer> supportedSuites(InetSocketAddress isa, int version) {
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

	public static void addCertificates(Set<String> serverCerts,
			InetSocketAddress isa, int version) {
		ServerHello sh = connect(isa, version,
				CipherSuiteUtil.CIPHER_SUITES.keySet());

		if (sh != null && sh.certificateChain != null) {
			for (Certificate cert : sh.certificateChain) {
				serverCerts.add(cert.toString());
			}
		}
	}

	static String versionString(int version) {
		if (version == 0x0200) {
			return "SSLv2";
		} else if (version == 0x0300) {
			return "SSLv3";
		} else if ((version >>> 8) == 0x03) {
			return "TLSv1." + ((version & 0xFF) - 1);
		} else {
			return String.format("UNKNOWN_VERSION:0x%04X", version);
		}
	}

	/*
	 * Connect to the server, send a ClientHello, and decode the response
	 * (ServerHello). On error, null is returned.
	 */
	static ServerHello connect(InetSocketAddress isa, int version,
			Collection<Integer> cipherSuites) {
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
	static ServerHelloSSLv2 connectV2(InetSocketAddress isa) {
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

	private static final SecureRandom RNG = new SecureRandom();

	/*
	 * Build a ClientHello message, with the specified maximum supported
	 * version, and list of cipher suites.
	 */
	static byte[] makeClientHello(int version, Collection<Integer> cipherSuites) {
		try {
			return makeClientHello0(version, cipherSuites);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	static byte[] makeClientHello0(int version, Collection<Integer> cipherSuites)
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

}
