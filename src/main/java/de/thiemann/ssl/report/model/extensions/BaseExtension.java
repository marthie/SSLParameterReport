package de.thiemann.ssl.report.model.extensions;

import de.thiemann.ssl.report.util.ASN1CertificateExtensionsIds;

public class BaseExtension {

    private ASN1CertificateExtensionsIds id;

    private byte[] extension;

    public ASN1CertificateExtensionsIds getId() {
        return id;
    }

    public void setId(ASN1CertificateExtensionsIds id) {
        this.id = id;
    }

    public byte[] getExtension() {
        return extension;
    }

    public void setExtension(byte[] extension) {
        this.extension = extension;
    }
}
