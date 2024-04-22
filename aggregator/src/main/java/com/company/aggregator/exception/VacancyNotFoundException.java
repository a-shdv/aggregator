package com.company.aggregator.exception;

public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException() {
    }

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
