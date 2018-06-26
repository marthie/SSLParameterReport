package de.thiemann.ssl.report.exceptions;

public class ReportOutputException extends Exception {

    public ReportOutputException(Throwable cause) {
        super(cause);
    }

    public ReportOutputException(String message) {
        super(message);
    }
}
