package de.thiemann.ssltest;

import java.io.IOException;
import java.io.OutputStream;

/*
 * A custom stream which encodes data bytes into SSL/TLS records (no
 * encryption).
 */
public class OutputRecord extends OutputStream {

	private static final int MAX_RECORD_LEN = 16384;
	
	private OutputStream out;
	private byte[] buffer = new byte[MAX_RECORD_LEN + 5];
	private int ptr;
	private int version;
	private int type;

	OutputRecord(OutputStream out) {
		this.out = out;
		ptr = 5;
	}

	void setType(int type) {
		this.type = type;
	}

	void setVersion(int version) {
		this.version = version;
	}

	public void flush() throws IOException {
		buffer[0] = (byte) type;
		Util.enc16be(version, buffer, 1);
		Util.enc16be(ptr - 5, buffer, 3);
		out.write(buffer, 0, ptr);
		out.flush();
		ptr = 5;
	}

	public void write(int b) throws IOException {
		buffer[ptr++] = (byte) b;
		if (ptr == buffer.length) {
			flush();
		}
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		while (len > 0) {
			int clen = Math.min(buffer.length - ptr, len);
			System.arraycopy(buf, off, buffer, ptr, clen);
			ptr += clen;
			off += clen;
			len -= clen;
			if (ptr == buffer.length) {
				flush();
			}
		}
	}
}

