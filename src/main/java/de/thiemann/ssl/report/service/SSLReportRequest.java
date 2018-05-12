package de.thiemann.ssl.report.service;

public class SSLReportRequest {

    private String host;
    private String port;

    public SSLReportRequest() {
    }

    public SSLReportRequest(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
