package com.company.aggregator.exceptions;

public class VacancyNotFoundException extends Exception {
    public VacancyNotFoundException() {
    }

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
