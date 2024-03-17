package com.company.parser.rabbitmq.exceptions;

public class RabbitException extends RuntimeException {

    public RabbitException(String message) {
        super(message);
    }

}
