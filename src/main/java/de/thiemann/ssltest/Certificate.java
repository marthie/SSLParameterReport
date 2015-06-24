package de.thiemann.ssltest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
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

	public Certificate(int i, byte[] ec) {
		this.order = new Integer(i);
		this.ec = ec;
		this.certificate = null;
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
		X500Name subjectName = new X500Name(
				subject.getName(X500Principal.RFC2253));

		sb.append(NL).append("Subject: ").append(subjectName.toString());

		// alternative names
		Collection<List<?>> alternativeNames = null;
		try {
			alternativeNames = cert.getSubjectAlternativeNames();
		} catch (CertificateParsingException e) {
			e.printStackTrace();
		}

		if (alternativeNames != null)
			sb.append(NL).append("Alternative Names: ")
					.append(getAlternativeNamesString(alternativeNames));

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
			sb.append(NL).append("Key: ")
					.append(getKeyString(pubKey.getEncoded()));
		}

		// issuer
		X500Principal issuer = cert.getIssuerX500Principal();
		X500Name issuerName = new X500Name(
				issuer.getName(X500Principal.RFC2253));
		sb.append(NL).append("Issuer: ").append(issuerName.toString());

		// signature algorithm
		sb.append(NL).append("Signature Algorithm: ").append(getSignatureAlgorithmString(cert.getSigAlgOID()));
		
		// fingerprint
		sb.append(NL).append("Fingerprint: ").append(doSHA1(ec));
		
		sb.append(NL)
				.append("================================================================================");

		return sb.toString();
	}

	public static String getAlternativeNamesString(
			Collection<List<?>> alternativeNames) {
		if (alternativeNames == null)
			return new String();

		StringBuffer sb = new StringBuffer();
		for (List<?> entry : alternativeNames) {
			sb.append(' ').append(entry.get(1).toString()).append(',');
		}

		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String getKeyString(byte[] encodedPublicKey) {
		StringBuffer sb = new StringBuffer();

		try {
			SubjectPublicKeyInfo subPubKeyInfo = new SubjectPublicKeyInfo(
					(ASN1Sequence) ASN1Object.fromByteArray(encodedPublicKey));
			String asn1PubKeyId = subPubKeyInfo.getAlgorithmId().getAlgorithm()
					.getId();

			if (asn1PubKeyId.equals(ASN1PublicKeyIds.RSA.getOid())) {
				DERSequence seq = (DERSequence) subPubKeyInfo.getPublicKey();
				ASN1Integer iModulus = (ASN1Integer) seq.getObjectAt(0);
				BigInteger modulus = iModulus.getPositiveValue();

				sb.append(ASN1PublicKeyIds.RSA.name()).append(" (")
						.append(modulus.bitLength()).append(')');
			} else if (asn1PubKeyId.equals(ASN1PublicKeyIds.DSA.getOid())) {
				sb.append(ASN1PublicKeyIds.DSA.name());
			} else if (asn1PubKeyId.equals(ASN1PublicKeyIds.Diffie_Hellman
					.getOid())) {
				sb.append(ASN1PublicKeyIds.Diffie_Hellman.name());
			} else if (asn1PubKeyId.equals(ASN1PublicKeyIds.KEA.getOid())) {
				sb.append(ASN1PublicKeyIds.KEA.name());
			} else if (asn1PubKeyId.equals(ASN1PublicKeyIds.ECDH.getOid())) {
				sb.append(ASN1PublicKeyIds.ECDH.name());
			} else
				sb.append("Unknown public key! OID: ").append(asn1PubKeyId);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	
	public static String getSignatureAlgorithmString(String oid) {
		StringBuffer sb = new StringBuffer();


			if (ASN1SignatureAlgorithmsIds.MD2.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.MD2.name());
			} else if (ASN1SignatureAlgorithmsIds.MD5.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.MD5.name());
			} else if (ASN1SignatureAlgorithmsIds.SHA1.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.SHA1.name());
			} else if (ASN1SignatureAlgorithmsIds.MD2WithRSAEncryption.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.MD2WithRSAEncryption.name());
			} else if (ASN1SignatureAlgorithmsIds.MD5WithRSAEncryption.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.MD5WithRSAEncryption.name());
			} else if (ASN1SignatureAlgorithmsIds.SHA1WithRSAEncryption.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.SHA1WithRSAEncryption.name());
			} else if (ASN1SignatureAlgorithmsIds.DSAWithSHA1.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.DSAWithSHA1.name());
			} else if (ASN1SignatureAlgorithmsIds.ECDSAWithSHA1.getOid().equals(oid)) {
				sb.append(ASN1SignatureAlgorithmsIds.ECDSAWithSHA1.name());
			}else
				sb.append("Unknown signature algorithm! OID: ").append(oid);

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
