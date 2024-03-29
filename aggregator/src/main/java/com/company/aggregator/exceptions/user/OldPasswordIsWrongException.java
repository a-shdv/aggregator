package com.company.aggregator.exceptions.user;

public class OldPasswordIsWrongException extends UserException {
    public OldPasswordIsWrongException(String message) {
        super(message);
    }
}
