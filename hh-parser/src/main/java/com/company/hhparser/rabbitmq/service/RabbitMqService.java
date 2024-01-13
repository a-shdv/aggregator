package com.company.hhparser.rabbitmq.service;

import com.company.hhparser.rabbitmq.dto.SendMessageDto;
import com.company.hhparser.rabbitmq.property.RabbitMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableRabbit
public class RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;

    public RabbitMqService(RabbitTemplate rabbitTemplate, RabbitMqProperties rabbitProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
    }

    public void send(SendMessageDto message) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), message);
    }
}

