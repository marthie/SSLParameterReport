package de.thiemann.ssl.report.exceptions;

public class ParsingSSLv2ServerHelloException extends Exception {

    public ParsingSSLv2ServerHelloException(String message) {
        super(message);
    }

    public ParsingSSLv2ServerHelloException(Throwable cause) {
        super(cause);
    }
}
