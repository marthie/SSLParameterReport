package de.thiemann.ssl.report.model;

/*
 * This class represents the response of a server which knows $ SSLv2. It
 * includes the list of cipher suites, and the identification of the server
 * certificate.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import de.thiemann.ssl.report.util.CertificateUtil;
import de.thiemann.ssl.report.util.IOUtil;

public class ServerHelloSSLv2 {

	private int[] cipherSuites;
	private String serverCertName;
	private String serverCertHash;

	public ServerHelloSSLv2(InputStream in) throws IOException {
		// assume a the 2-byte record without padding
		byte[] buf = new byte[2];
		IOUtil.readFully(in, buf);

		// check for record without padding
		int len = IOUtil.dec16be(buf, 0);
		if ((len & 0x8000) == 0) {
			throw new IOException("not a SSLv2 record");
		}

		// read record length
		len &= 0x7FFF;
		if (len < 11) {
			throw new IOException("not a SSLv2 server hello");
		}

		/*
		 * buf[0] = char MSG-SERVER-HELLO
		 * 
		 * buf[1] = char SESSION-ID-HIT
		 * 
		 * buf[2] = char CERTIFICATE-TYPE
		 * 
		 * buf[3] = char SERVER-VERSION-MSB
		 * 
		 * buf[4] = char SERVER-VERSION-LSB
		 * 
		 * buf[5] = char CERTIFICATE-LENGTH-MSB
		 * 
		 * buf[6] = char CERTIFICATE-LENGTH-LSB
		 * 
		 * buf[7] = char CIPHER-SPECS-LENGTH-MSB
		 * 
		 * buf[8] = char CIPHER-SPECS-LENGTH-LSB
		 * 
		 * buf[9] = char CONNECTION-ID-LENGTH-MSB
		 * 
		 * buf[10] = char CONNECTION-ID-LENGTH-LSB
		 */
		buf = new byte[11];
		IOUtil.readFully(in, buf);
		if (buf[0] != 0x04) {
			throw new IOException("not a SSLv2 server hello");
		}

		// read certificate data length
		int certLen = IOUtil.dec16be(buf, 5);

		// read cipher suites data length
		int csLen = IOUtil.dec16be(buf, 7);

		// read connection id data length
		int connIdLen = IOUtil.dec16be(buf, 9);

		// check server hello message
		if (len != 11 + certLen + csLen + connIdLen) {
			throw new IOException("not a SSLv2 server hello");
		}

		if (csLen == 0 || csLen % 3 != 0) {
			throw new IOException("not a SSLv2 server hello");
		}

		/*
		 * char CERTIFICATE-DATA[MSB<<8|LSB]
		 * 
		 * char CIPHER-SPECS-DATA[MSB<<8|LSB]
		 * 
		 * char CONNECTION-ID-DATA[MSB<<8|LSB]
		 */

		byte[] cert = new byte[certLen];
		IOUtil.readFully(in, cert);
		byte[] cs = new byte[csLen];
		IOUtil.readFully(in, cs);
		byte[] connId = new byte[connIdLen];
		IOUtil.readFully(in, connId);

		cipherSuites = new int[csLen / 3];
		for (int i = 0, j = 0; i < csLen; i += 3, j++) {
			cipherSuites[j] = IOUtil.dec24be(cs, i);
		}
		
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate xc = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(cert));
			serverCertName = xc.getSubjectX500Principal().toString();
			serverCertHash = CertificateUtil.computeFingerprint(cert);
		} catch (CertificateException e) {
			// ignored
		}
	}

	public int[] getCipherSuites() {
		return cipherSuites;
	}

	public void setCipherSuites(int[] cipherSuites) {
		this.cipherSuites = cipherSuites;
	}

	public String getServerCertName() {
		return serverCertName;
	}

	public void setServerCertName(String serverCertName) {
		this.serverCertName = serverCertName;
	}

	public String getServerCertHash() {
		return serverCertHash;
	}

	public void setServerCertHash(String serverCertHash) {
		this.serverCertHash = serverCertHash;
	}
}
