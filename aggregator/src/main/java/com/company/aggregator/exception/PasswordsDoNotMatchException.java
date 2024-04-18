package com.company.aggregator.exception;

public class PasswordsDoNotMatchException extends Exception {
    public PasswordsDoNotMatchException() {
    }

    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
