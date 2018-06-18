package de.thiemann.ssl.report.model;

public class ExtensionInfo {

    private String oid;
    private String description;
    private boolean isCritical;

    public ExtensionInfo() {
    }

    public ExtensionInfo(String oid, String description, boolean isCritical) {
        this.oid = oid;
        this.description = description;
        this.isCritical = isCritical;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }
}
