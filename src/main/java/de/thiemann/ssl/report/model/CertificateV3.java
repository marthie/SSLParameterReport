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

    private static String NL = System.getProperty("line.separator");

    private static Logger log = LoggerFactory.getLogger(CertificateV3.class);

    private byte[] ec;

    private BigInteger certificateVersion;
    private String subjectName;
    private List<String> alternativeNames;
    private long notBefore = 0L;
    private long notAfter = 0L;
    private PubKeyInfo pubKeyInfo;
    private String issuerName;
    private String signatureAlgorithm;
    private String fingerprint;
    private List<String> crlDistributionPoints;
    private Map<ASN1CertificateExtensionsIds, BaseExtension> certificateExtensions;

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

        // certificate version
        this.certificateVersion = x509Certificate.getVersion().getValue().add(BigInteger.valueOf(1));

        // subject
        this.subjectName = x509Certificate.getSubject().toString();

        // alternative names
        this.alternativeNames = new ArrayList<>();


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
                    .append(ListUtil.stringListToString(this.alternativeNames));

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
            if (this.pubKeyInfo.getPubKeyAlgorithm() != null)
                sb.append(NL).append("Key: ").append(this.pubKeyInfo.getPubKeyAlgorithm());

            if (this.pubKeyInfo.getPubKeySize() > 0)
                sb.append(" (").append(this.pubKeyInfo.getPubKeySize()).append(')');
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
            sb.append(ListUtil.stringListToString(this.crlDistributionPoints));
        else
            sb.append("No");

        sb.append(NL)
                .append("================================================================================");

        return sb.toString();
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

    public Map<ASN1CertificateExtensionsIds, BaseExtension> getCertificateExtensions() {
        return certificateExtensions;
    }

    public void setCertificateExtensions(Map<ASN1CertificateExtensionsIds, BaseExtension> certificateExtensions) {
        this.certificateExtensions = certificateExtensions;
    }
}
