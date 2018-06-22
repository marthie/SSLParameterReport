package de.thiemann.ssl.report.util;

import org.bouncycastle.asn1.x509.KeyUsage;

public enum CertificateKeyUsage {

    DigitalSignature("DigitalSignature", KeyUsage.digitalSignature),
    NonRepudiation("NonRepudiation", KeyUsage.nonRepudiation),
    KeyEncipherment("KeyEncipherment", KeyUsage.keyEncipherment),
    DataEncipherment("DataEncipherment", KeyUsage.dataEncipherment),
    KeyAgreement("KeyAgreement", KeyUsage.keyAgreement),
    KeyCertSign("KeyCertSign", KeyUsage.keyCertSign),
    CRLSign("CRLSign", KeyUsage.cRLSign),
    EncipherOnly("EncipherOnly", KeyUsage.encipherOnly),
    DecipherOnly("DecipherOnly", KeyUsage.decipherOnly);

    private String printableName;
    private int tag;

    CertificateKeyUsage(String printableName, int tag) {
        this.printableName = printableName;
        this.tag = tag;
    }

    public String getPrintableName() {
        return printableName;
    }

    public int getTag() {
        return tag;
    }
}
