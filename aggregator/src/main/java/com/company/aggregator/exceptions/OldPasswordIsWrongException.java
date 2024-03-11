package com.company.aggregator.exceptions;

public class OldPasswordIsWrongException extends Exception {
    public OldPasswordIsWrongException() {
    }

    public OldPasswordIsWrongException(String message) {
        super(message);
    }
}
