package de.thiemann.ssl.report.model.extensions;

import de.thiemann.ssl.report.util.ExtensionIdentifier;

public class BaseExtension {

    private ExtensionIdentifier id;

    private byte[] extension;

    public ExtensionIdentifier getId() {
        return id;
    }

    public void setId(ExtensionIdentifier id) {
        this.id = id;
    }

    public byte[] getExtension() {
        return extension;
    }

    public void setExtension(byte[] extension) {
        this.extension = extension;
    }
}
