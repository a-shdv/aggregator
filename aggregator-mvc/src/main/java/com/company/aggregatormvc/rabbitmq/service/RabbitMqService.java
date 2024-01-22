package com.company.aggregatormvc.rabbitmq.service;


import com.company.aggregatormvc.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregatormvc.rabbitmq.property.RabbitMqProperties;
import com.company.aggregatormvc.service.AggregatorService;
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
        log.info("RECEIVED: {}", receiveMessageDto.toString());
        aggregatorService.save(receiveMessageDto);
    }
}

