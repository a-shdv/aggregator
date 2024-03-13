package com.company.aggregator.exceptions;

public class FavouriteNotFoundException extends Exception {
    public FavouriteNotFoundException() {
    }

    public FavouriteNotFoundException(String message) {
        super(message);
    }
}
