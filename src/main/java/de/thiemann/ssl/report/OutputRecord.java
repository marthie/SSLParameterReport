package de.thiemann.ssl.report;

/*
 * A custom stream which encodes data bytes into SSL/TLS records (no
 * encryption).
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

import java.io.IOException;
import java.io.OutputStream;

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
		IOUtil.enc16be(version, buffer, 1);
		IOUtil.enc16be(ptr - 5, buffer, 3);
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
