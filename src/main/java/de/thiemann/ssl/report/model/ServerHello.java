package de.thiemann.ssl.report.model;

/*
 * This class decodes a ServerHello message from the server. The fields we
 * are interested in are stored in the package-accessible fields.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import de.thiemann.ssl.report.build.InputRecord;
import de.thiemann.ssl.report.util.IOUtil;

public class ServerHello {

	private int recordVersion;
	private int protoVersion;
	private long serverTime;
	private int cipherSuite;
	private boolean compression;
	private Set<Certificate> certificateChain;
	
	public static final int HANDSHAKE = 22;

	public ServerHello(InputStream in) throws IOException {
		InputRecord rec = new InputRecord(in);
		rec.setExpectedType(HANDSHAKE);

		/*
		 * First, get the handshake message header (4 bytes). First byte should
		 * be 2 ("ServerHello"), then comes the message size (over 3 bytes).
		 */
		byte[] buf = new byte[4];
		IOUtil.readFully(rec, buf);
		recordVersion = rec.getVersion();
		if (buf[0] != 2) {
			throw new IOException("unexpected handshake" + " message type: "
					+ (buf[0] & 0xFF));
		}
		buf = new byte[IOUtil.dec24be(buf, 1)];

		/*
		 * Read the complete message in RAM.
		 */
		IOUtil.readFully(rec, buf);
		int ptr = 0;

		/*
		 * The protocol version which we will use.
		 */
		if (ptr + 2 > buf.length) {
			throw new IOException("invalid ServerHello");
		}
		protoVersion = IOUtil.dec16be(buf, 0);
		ptr += 2;

		/*
		 * The server random begins with the server's notion of the current
		 * time.
		 */
		if (ptr + 32 > buf.length) {
			throw new IOException("invalid ServerHello");
		}
		serverTime = 1000L * (IOUtil.dec32be(buf, ptr) & 0xFFFFFFFFL);
		ptr += 32;

		/*
		 * We skip the session ID.
		 */
		if (ptr + 1 > buf.length) {
			throw new IOException("invalid ServerHello");
		}
		ptr += 1 + (buf[ptr] & 0xFF);

		/*
		 * The cipher suite and compression follow.
		 */
		if (ptr + 3 > buf.length) {
			throw new IOException("invalid ServerHello");
		}
		cipherSuite = IOUtil.dec16be(buf, ptr);
		compression = (buf[ptr + 2] & 0xFF) == 1 ? true : false;

		/*
		 * The ServerHello could include some extensions here, which we ignore.
		 */

		/*
		 * We now read a few extra messages, until we reach the server's
		 * Certificate message, or ServerHelloDone.
		 */
		for (;;) {
			buf = new byte[4];
			IOUtil.readFully(rec, buf);
			int mt = buf[0] & 0xFF;
			buf = new byte[IOUtil.dec24be(buf, 1)];
			IOUtil.readFully(rec, buf);
			switch (mt) {
			case 11:
				certificateChain = new TreeSet<Certificate>();
				processCertificate(buf);
				return;
			case 14:
				// ServerHelloDone
				return;
			}
		}
	}

	private void processCertificate(byte[] buf) {
		int ptr = 0;

		if (buf.length <= 6) {
			return;
		}

		// Certificate List byte size
		int len1 = IOUtil.dec24be(buf, 0);
		ptr += 3;

		if (len1 != buf.length - 3) {
			return;
		}

		// Output order of certificate chain
		int certOrder = 1;

		while (ptr < buf.length) {
			// certificate byte size
			int len2 = IOUtil.dec24be(buf, ptr);
			if (len2 > buf.length - ptr) {
				return;
			}

			ptr += 3;

			byte[] ec = new byte[len2];
			System.arraycopy(buf, ptr, ec, 0, len2);

			ptr += len2;

			CertificateV3 cert = new CertificateV3(certOrder, ec);
			certOrder += 1;

			certificateChain.add(cert);

		}
	}

	public int getRecordVersion() {
		return recordVersion;
	}

	public void setRecordVersion(int recordVersion) {
		this.recordVersion = recordVersion;
	}

	public int getProtoVersion() {
		return protoVersion;
	}

	public void setProtoVersion(int protoVersion) {
		this.protoVersion = protoVersion;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public int getCipherSuite() {
		return cipherSuite;
	}

	public void setCipherSuite(int cipherSuite) {
		this.cipherSuite = cipherSuite;
	}

	public boolean isCompression() {
		return compression;
	}

	public void setCompression(boolean compression) {
		this.compression = compression;
	}

	public Set<Certificate> getCertificateChain() {
		return certificateChain;
	}

	public void setCertificateChain(Set<Certificate> certificateChain) {
		this.certificateChain = certificateChain;
	}
}
