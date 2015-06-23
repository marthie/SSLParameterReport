package de.thiemann.ssltest;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Certificate implements Comparable<Integer> {

	static CertificateFactory cf = null;

	static {
		try {
			cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			e.printStackTrace();
		}
	}

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

		return hash = IOUtil.doSHA1(ec);
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

	@Override
	public int compareTo(Integer i) {
		return order.compareTo(i);
	}
}
