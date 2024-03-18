package com.company.aggregator.rabbitmq.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Data
@Primary
@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMqProperties {

    /**
     * Строка подключения к RabbitMQ.
     */
    private URI uri;

    /**
     * Адрес сервера RabbitMQ.
     */
    private List<Service> services;

    /**
     * Имя пользователя для подключения к RabbitMQ.
     */
    private String username;

    /**
     * Пароль пользователя для подключения к RabbitMQ.
     */
    private String password;

    /**
     * Наименование точки доступа в RabbitMQ.
     */
    private String topic;

    /**
     * Наименование очереди для отправки в RabbitMQ.
     */
    private String queueToSend0;
    private String queueToSend1;

    /**
     * Наименование очереди для приема в RabbitMQ.
     */
    private String queueToReceive;

    /**
     * Наименование routingKey для отправки в RabbitMQ.
     */
    private String routingKeyToSend0;

    private String routingKeyToSend1;

    /**
     * Наименование routingKey для приема в RabbitMQ.
     */
    private String routingKeyToReceive;

    /**
     * Параметры подключения к сервису RabbitMQ.
     */
    @Data
    public static class Service {

        /**
         * Адрес сервера RabbitMQ.
         */
        private String host;

        /**
         * Порт сервера RabbitMQ.
         */
        private int port;
    }

}
