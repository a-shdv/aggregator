package com.company.aggregator.rabbitmq.service;


import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.property.RabbitMqProperties;
import com.company.aggregator.service.AggregatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableRabbit
public class RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;
    private final AggregatorService aggregatorService;

    public RabbitMqService(RabbitTemplate rabbitTemplate, RabbitMqProperties rabbitProperties,
                           AggregatorService aggregatorService) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
        this.aggregatorService = aggregatorService;
    }

    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceiveMessageDto receiveMessageDto) {
        log.info("Received: {}", receiveMessageDto);
        aggregatorService.findAll(receiveMessageDto);
    }
}

