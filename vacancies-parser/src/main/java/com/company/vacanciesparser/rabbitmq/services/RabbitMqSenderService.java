package com.company.vacanciesparser.rabbitmq.services;

import com.company.vacanciesparser.rabbitmq.dtos.SendMessageDto;
import com.company.vacanciesparser.rabbitmq.properties.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableRabbit
@Slf4j
public class RabbitMqSenderService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;

    public void send(List<SendMessageDto> sendMessageDtos) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDtos);
        log.info("SENT: {}", sendMessageDtos);
    }
}
