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

import de.thiemann.ssl.report.model.extensions.BaseExtension;
import de.thiemann.ssl.report.util.*;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateV3 extends Certificate {

    private static Logger log = LoggerFactory.getLogger(CertificateV3.class);

    private byte[] ec;

    private BigInteger certificateVersion;
    private BigInteger certificateSerialNumber;
    private String subjectName;
    private List<String> subjectAlternativeNames;
    private List<String> issuerAlternativeNames;
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

        org.bouncycastle.asn1.x509.Certificate x509Certificate = null;
        try {
            x509Certificate = org.bouncycastle.asn1.x509.Certificate.getInstance(ASN1Sequence.fromByteArray(ec));
        } catch (IOException e) {
            log.error("Excrption: {}", e.getMessage());
            log.error("{}", e);
        }

        if(x509Certificate == null) {
            this.setProcessed(false);
            return this;
        }

        Extensions certificateExtensions = x509Certificate.getTBSCertificate().getExtensions();

        // certificate version
        this.certificateVersion = x509Certificate.getVersion().getValue().add(BigInteger.valueOf(1));

        // serial number
        this.certificateSerialNumber = x509Certificate.getSerialNumber().getValue();

        // subject
        this.subjectName = x509Certificate.getSubject().toString();

        // subject alternative names
        GeneralNames subjectAlternativeNames = GeneralNames.fromExtensions(certificateExtensions,
                ASN1CertificateExtensionsIds.SubjectAlternativeName.getASN1ObjectIdentifier());
        this.subjectAlternativeNames = CertificateUtil.transferGeneralNames(subjectAlternativeNames);

        // not before
        Date notBefore = x509Certificate.getStartDate().getDate();

        if (notBefore != null)
            this.notBefore = notBefore.getTime();

        // not after
        Date notAfter = x509Certificate.getEndDate().getDate();

        if (notAfter != null)
            this.notAfter = notAfter.getTime();

        // public key algorithm & size
        this.pubKeyInfo = CertificateUtil.transferPublicKeyInfo(x509Certificate.getSubjectPublicKeyInfo());

        // issuer name
        this.issuerName = x509Certificate.getIssuer().toString();

        // issuer alternative names
        GeneralNames issuerAlternativeNames = GeneralNames.fromExtensions(certificateExtensions,
                ASN1CertificateExtensionsIds.IssuerAlternativeName.getASN1ObjectIdentifier());
        this.issuerAlternativeNames = CertificateUtil.transferGeneralNames(issuerAlternativeNames);

        // signature algorithm
        AlgorithmIdentifier signatureAlgorithmIdentifier = x509Certificate.getSignatureAlgorithm();
        ASN1ObjectIdentifier signatureAlgorithmObjectIdentifier = signatureAlgorithmIdentifier.getAlgorithm();
        this.signatureAlgorithm = CertificateUtil.transferSignatureAlgorithm(signatureAlgorithmObjectIdentifier.getId());

        // fingerprint

        this.fingerprint = CertificateUtil.computeFingerprint(this.ec);

        // CRL Distribution Points

        /*
        byte[] extension = this.jseX509Cert
                .getExtensionValue(ASN1CertificateExtensionsIds.CRLDistributionPoints
                        .getOid());
                        */

        //this.crlDistributionPoints = CertificateUtil.transferDistributionPoints(extension);
        this.crlDistributionPoints = new ArrayList<>();

        return this;
    }

    public byte[] getEc() {
        return ec;
    }

    public void setEc(byte[] ec) {
        this.ec = ec;
    }

    public BigInteger getCertificateVersion() {
        return certificateVersion;
    }

    public void setCertificateVersion(BigInteger certificateVersion) {
        this.certificateVersion = certificateVersion;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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

    public BigInteger getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(BigInteger certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public List<String> getSubjectAlternativeNames() {
        return subjectAlternativeNames;
    }

    public void setSubjectAlternativeNames(List<String> subjectAlternativeNames) {
        this.subjectAlternativeNames = subjectAlternativeNames;
    }

    public List<String> getIssuerAlternativeNames() {
        return issuerAlternativeNames;
    }

    public void setIssuerAlternativeNames(List<String> issuerAlternativeNames) {
        this.issuerAlternativeNames = issuerAlternativeNames;
    }
}
