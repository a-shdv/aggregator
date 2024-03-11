package com.company.aggregator.exceptions;

public class PasswordsDoNotMatchException extends Exception {
    public PasswordsDoNotMatchException() {
    }

    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
