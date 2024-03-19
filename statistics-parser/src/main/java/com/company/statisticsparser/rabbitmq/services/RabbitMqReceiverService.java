package com.company.statisticsparser.rabbitmq.services;

import com.company.statisticsparser.rabbitmq.dtos.ReceiveMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@EnableRabbit
@Slf4j
public class RabbitMqReceiverService {

    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceiveMessageDto receiveMessageDto) {
        log.info("RECEIVED: {}", receiveMessageDto.toString());
        System.out.println();
    }
}
