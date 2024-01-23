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

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@EnableRabbit
@Slf4j
public class RabbitMqReceiverService {
    private final HabrParserService habrParserService;
    private final HhParserService hhParserService;

    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceiveMessageDto receiveMessageDto) {
        log.info("RECEIVED: {}", receiveMessageDto.toString());
        CompletableFuture.allOf(
                habrParserService
                        .findAllVacanciesByQuery(receiveMessageDto.getTitle()),
                hhParserService
                        .findAllVacancies(receiveMessageDto.getTitle()));
    }
}
