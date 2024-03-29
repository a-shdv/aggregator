package com.company.vacanciesparser.rabbitmq.exception;

public class RabbitException extends RuntimeException {

    public RabbitException(String message) {
        super(message);
    }

}
