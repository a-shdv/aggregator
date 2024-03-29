package com.company.statisticsparser.rabbitmq.service;

import com.company.statisticsparser.rabbitmq.dto.SendMessageDto;
import com.company.statisticsparser.rabbitmq.property.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableRabbit
@Slf4j
public class RabbitMqSenderService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;

    public void send(SendMessageDto sendMessageDtos) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDtos);
        log.info("SENT: {}", sendMessageDtos);
    }
}
