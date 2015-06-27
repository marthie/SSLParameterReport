package de.thiemann.ssl.report.util;

/*
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class CertificateUtil {
	/*
	 * Compute the SHA-1 hash of some bytes, returning the hash value in
	 * hexadecimal.
	 */
	public static String computeFingerprint(byte[] buf) {
		return doSHA1(buf, 0, buf.length);
	}
	
	public static String computeFingerprint(byte[] buf, int off, int len) {
		return doSHA1(buf, off, len);
	}

	private static String doSHA1(byte[] buf, int off, int len) {
		Formatter f = null;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(buf, off, len);
			byte[] fingerprintVector = md.digest();

			f = new Formatter();
			for (int i = 0; i < fingerprintVector.length; i++) {
				if (i == fingerprintVector.length - 1)
					f.format("%02x", fingerprintVector[i] & 0xFF);
				else
					f.format("%02x:", fingerprintVector[i] & 0xFF);
			}
			return f.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new Error(e);
		} finally {
			if (f != null)
				f.close();
		}
	}
}
