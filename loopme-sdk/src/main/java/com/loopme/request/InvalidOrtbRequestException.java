package com.loopme.request;

public class InvalidOrtbRequestException extends Exception {
    private final String request;

    public InvalidOrtbRequestException(String message, String request) {
        super(message);
        this.request = request;
    }

    public String getRequest() {
        return request;
    }
}