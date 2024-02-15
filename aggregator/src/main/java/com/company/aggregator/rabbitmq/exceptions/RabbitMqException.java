package com.company.aggregator.rabbitmq.exceptions;

public class RabbitMqException extends RuntimeException {

    public RabbitMqException(String message) {
        super(message);
    }

}
