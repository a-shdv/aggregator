package com.company.aggregator.exception.user;

public class OldPasswordIsWrongException extends UserException {
    public OldPasswordIsWrongException(String message) {
        super(message);
    }
}
