package de.thiemann.ssltest;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Formatter;

public class Certificate implements Comparable<Integer> {

	static CertificateFactory cf = null;

	static {
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}
	
	private static String NL = System.getProperty("line.separator");

	Integer order;
	byte[] ec;
	X509Certificate certificate;
	String name;
	String hash;

	public Certificate(int i, byte[] ec) {
		this.order = new Integer(i);
		this.ec = ec;
		this.certificate = null;
		this.name = null;
		this.hash = null;
	}

	public String getName() {
		if (name != null && !name.isEmpty())
			return name;

		return name = getX509Certificate().getSubjectX500Principal().toString();
	}

	public String getHash() {
		if (hash != null && !hash.isEmpty())
			return hash;

		return hash = doSHA1(ec);
	}

	public X509Certificate getX509Certificate() {
		if (this.certificate == null && cf != null) {
			try {
				this.certificate = (X509Certificate) cf
						.generateCertificate(new ByteArrayInputStream(ec));
			} catch (CertificateException e) {
				e.printStackTrace();
			}
		}

		return this.certificate;
	}

	public String toString() {

		return "(" + order.toString() + ")" + getHash() + ": " + getName();
	}
	
	private String report() {
		StringBuffer sb = new StringBuffer();
		
		X509Certificate cert = getX509Certificate();
		
		sb.append(NL).append("Order send by Server: ").append(order);
		
		return null;
	}

	@Override
	public int compareTo(Integer i) {
		return order.compareTo(i);
	}

	/*
	 * Compute the SHA-1 hash of some bytes, returning the hash value in
	 * hexadecimal.
	 */
	static String doSHA1(byte[] buf) {
		return doSHA1(buf, 0, buf.length);
	}

	static String doSHA1(byte[] buf, int off, int len) {
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
