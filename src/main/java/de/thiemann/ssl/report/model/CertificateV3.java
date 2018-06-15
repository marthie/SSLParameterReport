package de.thiemann.ssl.report.model;

/*
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
/*
 * Copyright (c) 2015  Marius Thiemann <marius dot thiemann at ploin dot de>
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import de.thiemann.ssl.report.util.ASN1CertificateExtensionsIds;
import de.thiemann.ssl.report.util.ASN1PublicKeyIds;
import de.thiemann.ssl.report.util.ASN1SignatureAlgorithmsIds;
import de.thiemann.ssl.report.util.CertificateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateV3 extends Certificate {

    private Logger log = LoggerFactory.getLogger(CertificateV3.class);

    static CertificateFactory cf = null;

    static {
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    private static String NL = System.getProperty("line.separator");

    private byte[] ec;
    private X509Certificate jseX509Cert;


    private int certificateVersion;
    private String subjectName;
    private List<String> alternativeNames;
    private long notBefore = 0L;
    private long notAfter = 0L;
    private PubKeyInfo pubKeyInfo;
    private String issuerName;
    private String signatureAlgorithm;
    private String fingerprint;
    private List<String> crlDistributionPoints;

    public CertificateV3(int i, byte[] ec) {
        super();
        this.setOrder(new Integer(i));
        this.ec = ec;
        this.setProcessed(false);
    }

    @Override
    public Certificate processCertificateBytes() {
        this.jseX509Cert = null;

        if (cf != null) {
            try {
                this.jseX509Cert = (X509Certificate) cf
                        .generateCertificate(new ByteArrayInputStream(ec));
            } catch (CertificateException e) {
                e.printStackTrace();
            }
        }

        // certificate version
        this.certificateVersion = this.jseX509Cert.getVersion();

        // common name
        X500Principal subject = this.jseX509Cert.getSubjectX500Principal();
        X500Name subjectName = new X500Name(
                subject.getName(X500Principal.RFC2253));
        this.subjectName = subjectName.toString();

        // alternative names
        try {
            Collection<List<?>> alternativeNames = this.jseX509Cert
                    .getSubjectAlternativeNames();
            this.alternativeNames = transferAlternativeNames(alternativeNames);
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        }

        // not before
        Date notBefore = this.jseX509Cert.getNotBefore();

        if (notBefore != null)
            this.notBefore = notBefore.getTime();

        // not after
        Date notAfter = this.jseX509Cert.getNotAfter();

        if (notAfter != null)
            this.notAfter = notAfter.getTime();

        // public key algorithm & size
        PublicKey pubKey = this.jseX509Cert.getPublicKey();

        if (pubKey != null)
            this.pubKeyInfo = transferPublicKeyInfo(pubKey.getEncoded());

        // issuer
        X500Principal issuer = this.jseX509Cert.getIssuerX500Principal();
        X500Name issuerName = new X500Name(
                issuer.getName(X500Principal.RFC2253));
        this.issuerName = issuerName.toString();

        // signature algorithm
        this.signatureAlgorithm = transferSignatureAlgorithm(this.jseX509Cert
                .getSigAlgOID());

        // fingerprint

        this.fingerprint = CertificateUtil.computeFingerprint(this.ec);

        // CRL Distribution Points

        byte[] extension = this.jseX509Cert
                .getExtensionValue(ASN1CertificateExtensionsIds.CRLDistributionPoints
                        .getOid());

        this.crlDistributionPoints = transferDistributionPoints(extension);

        return this;
    }

    @Override
    public String certificateReport() {
        if (!this.isProcessed()) {
            this.processCertificateBytes();
        }

        StringBuffer sb = new StringBuffer();

        sb.append(NL)
                .append("=========================== Server Certificate #")
                .append(this.getOrder()).append(" ==============================");

        // certificate version
        sb.append(NL).append("Certificate Version: ")
                .append(this.certificateVersion);

        // common name
        sb.append(NL).append("Subject: ").append(this.subjectName);

        // alternative names
        if (this.alternativeNames != null)
            sb.append(NL).append("Alternative Names: ")
                    .append(stringListToString(this.alternativeNames));

        // not before
        if (this.notBefore > 0L)
            sb.append(NL).append("Not Before: ")
                    .append(String.format("%1$tF %1$tT", this.notBefore));

        // not after
        if (this.notAfter > 0L)
            sb.append(NL).append("Not After: ")
                    .append(String.format("%1$tF %1$tT", this.notAfter));

        // public key algorithm & size
        if (this.pubKeyInfo != null) {
            if (this.pubKeyInfo.pubKeyAlgorithm != null)
                sb.append(NL).append("Key: ").append(this.pubKeyInfo.pubKeyAlgorithm);

            if (this.pubKeyInfo.pubKeySize > 0)
                sb.append(" (").append(this.pubKeyInfo.pubKeySize).append(')');
        }

        // issuer
        sb.append(NL).append("Issuer: ").append(this.issuerName);

        // signature algorithm
        sb.append(NL).append("Signature Algorithm: ")
                .append(this.signatureAlgorithm);

        // fingerprint
        sb.append(NL).append("Fingerprint: ")
                .append(this.fingerprint);

        // CRL Distribution Points

        sb.append(NL).append("CRL Distribution Points: ");
        if (this.crlDistributionPoints != null)
            sb.append(stringListToString(this.crlDistributionPoints));
        else
            sb.append("No");

        sb.append(NL)
                .append("================================================================================");

        return sb.toString();
    }

    private List<String> transferDistributionPoints(byte[] extension) {
        if (extension == null)
            return null;

        ASN1Sequence crlDistributionPoints = null;

        try {
            ASN1Object o = null;

            o = DEROctetString.fromByteArray(extension);
            if (o instanceof DEROctetString) {
                DEROctetString octStr = (DEROctetString) o;

                o = ASN1Sequence.fromByteArray(octStr.getOctets());
                if (o instanceof ASN1Sequence) {
                    crlDistributionPoints = (ASN1Sequence) o;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (crlDistributionPoints == null)
            return null;

        List<String> l = new ArrayList<String>();
        Enumeration<?> e = crlDistributionPoints.getObjects();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();

            if (o instanceof ASN1Sequence) {
                ASN1Sequence seqDP = (ASN1Sequence) o;
                DistributionPoint dp = new DistributionPoint(seqDP);

                DistributionPointName dpn = dp.getDistributionPoint();
                ASN1Encodable enc = dpn.getName();

                if (enc instanceof GeneralNames) {
                    GeneralNames gns = (GeneralNames) enc;

                    for (GeneralName gn : gns.getNames()) {
                        l.add(gn.toString());
                    }
                }
            }
        }

        if (!l.isEmpty())
            return l;
        else
            return null;
    }

    public static String stringListToString(List<String> stringArray) {
        if (stringArray == null)
            return new String();

        StringBuffer sb = new StringBuffer();
        for (String entry : stringArray) {
            sb.append(' ').append(entry).append(',');
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private List<String> transferAlternativeNames(
            Collection<List<?>> alternativeNames) {
        if (alternativeNames == null)
            return null;

        List<String> l = new ArrayList<String>();
        for (List<?> entry : alternativeNames) {
            l.add(entry.get(1).toString());
        }

        return l;
    }

    private PubKeyInfo transferPublicKeyInfo(byte[] encodedPublicKey) {
        PubKeyInfo info = new PubKeyInfo();

        try {
            SubjectPublicKeyInfo subPubKeyInfo = new SubjectPublicKeyInfo(
                    (ASN1Sequence) ASN1Sequence.fromByteArray(encodedPublicKey));
            String asn1PubKeyId = subPubKeyInfo.getAlgorithmId().getAlgorithm()
                    .getId();

            if (asn1PubKeyId.equals(ASN1PublicKeyIds.RSA.getOid())) {
                DLSequence seq = (DLSequence) subPubKeyInfo.getPublicKey();
                ASN1Integer iModulus = (ASN1Integer) seq.getObjectAt(0);
                BigInteger modulus = iModulus.getPositiveValue();

                info.pubKeyAlgorithm = ASN1PublicKeyIds.RSA.name();
                info.pubKeySize = modulus.bitLength();
            } else if (asn1PubKeyId.equals(ASN1PublicKeyIds.DSA.getOid())) {
                info.pubKeyAlgorithm = ASN1PublicKeyIds.DSA.name();
            } else if (asn1PubKeyId.equals(ASN1PublicKeyIds.Diffie_Hellman
                    .getOid())) {
                info.pubKeyAlgorithm = ASN1PublicKeyIds.Diffie_Hellman.name();
            } else if (asn1PubKeyId.equals(ASN1PublicKeyIds.KEA.getOid())) {
                info.pubKeyAlgorithm = ASN1PublicKeyIds.KEA.name();
            } else if (asn1PubKeyId.equals(ASN1PublicKeyIds.ECDH.getOid())) {
                info.pubKeyAlgorithm = ASN1PublicKeyIds.ECDH.name();
            } else
                info.pubKeyAlgorithm = "Unknown public key! OID: " + asn1PubKeyId;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static String transferSignatureAlgorithm(String oid) {
        StringBuffer sb = new StringBuffer();

        boolean foundAlgorithm = false;
        for (ASN1SignatureAlgorithmsIds sa : ASN1SignatureAlgorithmsIds
                .values()) {
            if (sa.getOid().equals(oid)) {
                sb.append(sa.name()).append(" (").append(oid).append(')');
                foundAlgorithm = true;
                break;
            }
        }

        if (!foundAlgorithm)
            sb.append("Unknown signature algorithm! OID: ").append(oid);

        return sb.toString();
    }

    public byte[] getEc() {
        return ec;
    }

    public void setEc(byte[] ec) {
        this.ec = ec;
    }

    public X509Certificate getJseX509Cert() {
        return jseX509Cert;
    }

    public void setJseX509Cert(X509Certificate jseX509Cert) {
        this.jseX509Cert = jseX509Cert;
    }

    public int getCertificateVersion() {
        return certificateVersion;
    }

    public void setCertificateVersion(int certificateVersion) {
        this.certificateVersion = certificateVersion;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public List<String> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(List<String> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public long getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(long notBefore) {
        this.notBefore = notBefore;
    }

    public long getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(long notAfter) {
        this.notAfter = notAfter;
    }

    public PubKeyInfo getPubKeyInfo() {
        return pubKeyInfo;
    }

    public void setPubKeyInfo(PubKeyInfo pubKeyInfo) {
        this.pubKeyInfo = pubKeyInfo;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public List<String> getCrlDistributionPoints() {
        return crlDistributionPoints;
    }

    public void setCrlDistributionPoints(List<String> crlDistributionPoints) {
        this.crlDistributionPoints = crlDistributionPoints;
    }

    public static class PubKeyInfo {
        private String pubKeyAlgorithm;
        private int pubKeySize = 0;

        public String getPubKeyAlgorithm() {
            return pubKeyAlgorithm;
        }

        public void setPubKeyAlgorithm(String pubKeyAlgorithm) {
            this.pubKeyAlgorithm = pubKeyAlgorithm;
        }

        public int getPubKeySize() {
            return pubKeySize;
        }

        public void setPubKeySize(int pubKeySize) {
            this.pubKeySize = pubKeySize;
        }
    }
}
