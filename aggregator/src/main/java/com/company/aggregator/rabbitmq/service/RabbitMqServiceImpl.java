package com.company.aggregator.rabbitmq.service;


import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dto.SendMessageDto;
import com.company.aggregator.rabbitmq.property.RabbitMqProperties;
import com.company.aggregator.service.AggregatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@EnableRabbit
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;
    private final AggregatorService aggregatorService;

    public RabbitMqServiceImpl(RabbitTemplate rabbitTemplate, RabbitMqProperties rabbitProperties,
                               AggregatorService aggregatorService) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
        this.aggregatorService = aggregatorService;
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceiveMessageDto receiveMessageDto) {
        log.info("RECEIVED: {}", receiveMessageDto.toString());
        aggregatorService
                .findBySource(receiveMessageDto.getSource())
                .thenAccept(vacancy -> {
                    if (vacancy != null) {
                        log.info("Vacancy already exists: {}", vacancy.getSource());
                    } else {
                        aggregatorService.save(receiveMessageDto);
                    }
                });
    }

    @Override
    public void send(SendMessageDto sendMessageDto) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}

