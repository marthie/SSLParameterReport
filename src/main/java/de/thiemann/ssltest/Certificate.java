package de.thiemann.ssltest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Formatter;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

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
		return certificateReport();
	}

	public String certificateReport() {
		StringBuffer sb = new StringBuffer();

		X509Certificate cert = getX509Certificate();

		sb.append(NL)
				.append("=========================== Server Certificate #")
				.append(order).append(" ==============================");

		// certificate version
		sb.append(NL).append("Certificate Version: ").append(cert.getVersion());

		// common name
		X500Principal subject = cert.getSubjectX500Principal();
		X500Name name = new X500Name(subject.getName(X500Principal.RFC2253));

		RDN cn = name.getRDNs(BCStyle.CN)[0];

		if (cn != null)
			sb.append(NL).append("Common Name: ")
					.append(cn.getFirst().getValue());

		// not before
		Date notBefore = cert.getNotBefore();

		if (notBefore != null)
			sb.append(NL).append("Not Before: ")
					.append(String.format("%1$tF %1$tT", notBefore));

		// not after
		Date notAfter = cert.getNotAfter();

		if (notAfter != null)
			sb.append(NL).append("Not After: ")
					.append(String.format("%1$tF %1$tT", notAfter));

		// public key algorithm & size
		PublicKey pubKey = cert.getPublicKey();

		if (pubKey != null) {
			sb.append(NL).append("Public Key Information: ");

			try {
				SubjectPublicKeyInfo subPubKeyInfo = new SubjectPublicKeyInfo(
						(ASN1Sequence) ASN1Object.fromByteArray(pubKey
								.getEncoded()));
				String asn1Id = subPubKeyInfo.getAlgorithmId().getAlgorithm()
						.getId();

				if (asn1Id.equals(ASN1PublicKeyIds.RSA.getId())) {
					DERSequence seq = (DERSequence) subPubKeyInfo
							.getPublicKey();
					ASN1Integer iModulus = (ASN1Integer) seq.getObjectAt(0);
					BigInteger modulus = iModulus.getPositiveValue();

					sb.append(ASN1PublicKeyIds.RSA.name()).append(" (")
							.append(modulus.bitLength()).append(')');
				} else if (asn1Id.equals(ASN1PublicKeyIds.DSA.getId())) {
					sb.append(ASN1PublicKeyIds.DSA.name());
				} else if (asn1Id.equals(ASN1PublicKeyIds.Diffie_Hellman
						.getId())) {
					sb.append(ASN1PublicKeyIds.Diffie_Hellman.name());
				} else if (asn1Id.equals(ASN1PublicKeyIds.KEA.getId())) {
					sb.append(ASN1PublicKeyIds.KEA.name());
				} else if (asn1Id.equals(ASN1PublicKeyIds.ECDH.getId())) {
					sb.append(ASN1PublicKeyIds.ECDH.name());
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		sb.append(NL)
				.append("================================================================================");

		return sb.toString();
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
