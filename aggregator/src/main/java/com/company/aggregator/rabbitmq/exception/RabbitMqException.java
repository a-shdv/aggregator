package com.company.aggregator.rabbitmq.exception;

public class RabbitMqException extends RuntimeException {

    public RabbitMqException(String message) {
        super(message);
    }

}
