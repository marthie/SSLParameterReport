package de.thiemann.ssl.report;

/*
 * A custom stream which expects SSL/TLS records (no encryption) and
 * rebuilds the encoded data stream. Incoming records MUST have the expected
 * type (e.g. "handshake"); alert messages are skipped.
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
import java.io.InputStream;

public class InputRecord extends InputStream {

	private static final int MAX_RECORD_LEN = 16384;
	private static final int ALERT = 21;

	private InputStream in;
	private byte[] buffer = new byte[MAX_RECORD_LEN + 5];
	private int ptr, end;
	private int version;
	private int type;
	private int expectedType;

	InputRecord(InputStream in) {
		this.in = in;
		ptr = 0;
		end = 0;
	}

	void setExpectedType(int expectedType) {
		this.expectedType = expectedType;
	}

	int getVersion() {
		return version;
	}

	private void refill() throws IOException {
		for (;;) {
			IOUtil.readFully(in, buffer, 0, 5);
			type = buffer[0] & 0xFF;
			version = IOUtil.dec16be(buffer, 1);
			end = IOUtil.dec16be(buffer, 3);
			IOUtil.readFully(in, buffer, 0, end);
			ptr = 0;
			if (type != expectedType) {
				if (type == ALERT) {
					/*
					 * We just ignore alert messages.
					 */
					continue;
				}
				throw new IOException("unexpected record type: " + type);
			}
			return;
		}
	}

	public int read() throws IOException {
		while (ptr == end) {
			refill();
		}
		return buffer[ptr++] & 0xFF;
	}

	public int read(byte[] buf, int off, int len) throws IOException {
		while (ptr == end) {
			refill();
		}
		int clen = Math.min(end - ptr, len);
		System.arraycopy(buffer, ptr, buf, off, clen);
		ptr += clen;
		return clen;
	}
}
