package com.company.aggregator.exception;

public class OldPasswordIsWrongException extends Exception {
    public OldPasswordIsWrongException() {
    }

    public OldPasswordIsWrongException(String message) {
        super(message);
    }
}
