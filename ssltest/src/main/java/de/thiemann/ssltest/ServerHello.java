package de.thiemann.ssltest;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/*
 * This class decodes a ServerHello message from the server. The fields we
 * are interested in are stored in the package-accessible fields.
 */
public class ServerHello {

	int recordVersion;
	int protoVersion;
	long serverTime;
	int cipherSuite;
	boolean compression;
	List<Certificate> certificateChain;

	ServerHello(InputStream in) throws IOException {
		InputRecord rec = new InputRecord(in);
		rec.setExpectedType(TestSSLServer.HANDSHAKE);

		/*
		 * First, get the handshake message header (4 bytes). First byte should
		 * be 2 ("ServerHello"), then comes the message size (over 3 bytes).
		 */
		byte[] buf = new byte[4];
		Util.readFully(rec, buf);
		recordVersion = rec.getVersion();
		if (buf[0] != 2) {
			throw new IOException("unexpected handshake" + " message type: "
					+ (buf[0] & 0xFF));
		}
		buf = new byte[Util.dec24be(buf, 1)];

		/*
		 * Read the complete message in RAM.
		 */
		Util.readFully(rec, buf);
		int ptr = 0;

		/*
		 * The protocol version which we will use.
		 */
		if (ptr + 2 > buf.length) {
			throw new IOException("invalid ServerHello");
		}
		protoVersion = Util.dec16be(buf, 0);
		ptr += 2;

		/*
		 * The server random begins with the server's notion of the current
		 * time.
		 */
		if (ptr + 32 > buf.length) {
			throw new IOException("invalid ServerHello");
		}
		serverTime = 1000L * (Util.dec32be(buf, ptr) & 0xFFFFFFFFL);
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
		cipherSuite = Util.dec16be(buf, ptr);
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
			Util.readFully(rec, buf);
			int mt = buf[0] & 0xFF;
			buf = new byte[Util.dec24be(buf, 1)];
			Util.readFully(rec, buf);
			switch (mt) {
			case 11:
				certificateChain = new ArrayList<Certificate>();
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

		int len1 = Util.dec24be(buf, 0);
		ptr += 3;

		if (len1 != buf.length - 3) {
			return;
		}

		int certOrder = 1;

		while (ptr < buf.length) {
			int len2 = Util.dec24be(buf, ptr);
			if (len2 > buf.length - ptr) {
				return;
			}

			ptr += 3;

			byte[] ec = new byte[len2];
			System.arraycopy(buf, ptr, ec, 0, len2);

			ptr += len2;
			try {
				Certificate cert = new Certificate(certOrder, ec);
				certOrder += 1;

				certificateChain.add(cert);
			} catch (CertificateException e) {
				// ignored
			}
		}
	}
}
