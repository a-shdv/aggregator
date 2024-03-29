package com.company.aggregator.exceptions.user;

public class PasswordsDoNotMatchException extends UserException {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }
}
