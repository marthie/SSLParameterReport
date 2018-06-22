package de.thiemann.ssl.report.util;

/*

The MIT License (MIT)

Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.Extension;


public enum ExtensionIdentifier {

    AuditIdentity(Extension.auditIdentity, "AuditIdentity"),
    AuthorityInfoAccess(Extension.authorityInfoAccess, "AuthorityInfoAccess"),
    AuthorityKeyIdentifier(Extension.authorityKeyIdentifier, "AuthorityKeyIdentifier"),
    BasicConstraints(Extension.basicConstraints, "BasicConstraints"),
    BiometricInfo(Extension.biometricInfo, "BiometricInfo"),
    CertificateIssuer(Extension.certificateIssuer, "CertificateIssuer"),
    CertificatePolicies(Extension.certificatePolicies, "CertificatePolicies"),
    CRLDistributionPoints(Extension.cRLDistributionPoints, "CRLDistributionPoints"),
    CRLNumber(Extension.cRLNumber, "CRLNumber"),
    DeltaCRLIndicator(Extension.deltaCRLIndicator, "DeltaCRLIndicator"),
    ExpiredCertsOnCRL(Extension.expiredCertsOnCRL, "ExpiredCertsOnCRL"),
    ExtendedKeyUsage(Extension.extendedKeyUsage, "ExtendedKeyUsage"),
    FreshestCRL(Extension.freshestCRL, "FreshestCRL"),
    InhibitAnyPolicy(Extension.inhibitAnyPolicy, "InhibitAnyPolicy"),
    InstructionCode(Extension.instructionCode, "InstructionCode"),
    InvalidityDate(Extension.invalidityDate, "InvalidityDate"),
    IssuerAlternativeName(Extension.issuerAlternativeName, "IssuerAlternativeName"),
    IssuingDistributionPoint(Extension.issuingDistributionPoint, "IssuingDistributionPoint"),
    KeyUsage(Extension.keyUsage, "KeyUsage"),
    LogoType(Extension.logoType, "LogoType"),
    NameConstraints(Extension.nameConstraints, "NameConstraints"),
    NoRevAvail(Extension.noRevAvail, "NoRevAvail"),
    PolicyConstraints(Extension.policyConstraints, "PolicyConstraints"),
    PolicyMappings(Extension.policyMappings, "PolicyMappings"),
    PrivateKeyUsagePeriod(Extension.privateKeyUsagePeriod, "PrivateKeyUsagePeriod"),
    QCStatements(Extension.qCStatements, "QCStatements"),
    ReasonCode(Extension.reasonCode, "ReasonCode"),
    SubjectAlternativeName(Extension.subjectAlternativeName, "SubjectAlternativeName"),
    SubjectDirectoryAttributes(Extension.subjectDirectoryAttributes, "SubjectDirectoryAttributes"),
    SubjectInfoAccess(Extension.subjectInfoAccess, "SubjectInfoAccess"),
    SubjectKeyIdentifier(Extension.subjectKeyIdentifier, "SubjectKeyIdentifier"),
    TargetInformation(Extension.targetInformation, "TargetInformation");

    private ASN1ObjectIdentifier id;
    private String description;

    private ExtensionIdentifier(ASN1ObjectIdentifier id, String description) {
        this.id = id;
        this.description = description;
    }

    public ASN1ObjectIdentifier getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
