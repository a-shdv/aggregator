package com.company.parser.rabbitmq.service;

import com.company.parser.rabbitmq.dto.ReceiveMessageDto;
import com.company.parser.rabbitmq.dto.SendMessageDto;
import com.company.parser.rabbitmq.properties.RabbitMqProperties;
import com.company.parser.service.HabrParserService;
import com.company.parser.service.HhParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableRabbit
@Slf4j
public class RabbitMqSenderService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;

    public void send(SendMessageDto sendMessageDto) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}
