package de.thiemann.ssl.report.service;

import java.util.Date;

public class ErrorResponse {

    private long timestamp;
    private String type;
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String type, String message) {
        this.timestamp = System.currentTimeMillis();
        this.type = type;
        this.message = message;
    }

    public ErrorResponse(Exception e) {
        this.timestamp = System.currentTimeMillis();
        this.type = e.getClass().getSimpleName();
        this.message = e.getMessage();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
