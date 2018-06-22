package de.thiemann.ssl.report.model;

public class PubKeyInfo {
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