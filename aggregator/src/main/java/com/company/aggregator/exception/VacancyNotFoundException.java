package com.company.aggregator.exception;

public class VacancyNotFoundException extends Exception {
    public VacancyNotFoundException() {
    }

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
