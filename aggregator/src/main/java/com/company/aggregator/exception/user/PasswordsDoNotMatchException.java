package com.company.aggregator.exception.user;

public class PasswordsDoNotMatchException extends UserException {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
