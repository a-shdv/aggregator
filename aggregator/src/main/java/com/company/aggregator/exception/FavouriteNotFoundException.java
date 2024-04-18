package com.company.aggregator.exception;

public class FavouriteNotFoundException extends Exception {
    public FavouriteNotFoundException() {
    }

    public FavouriteNotFoundException(String message) {
        super(message);
    }
}
