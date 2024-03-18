package com.company.statisticsparser.rabbitmq.services;


import com.company.statisticsparser.rabbitmq.dtos.ReceiveMessageDto;
import com.company.statisticsparser.rabbitmq.dtos.SendMessageDto;
import com.company.statisticsparser.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMqServiceImpl implements RabbitMqService {
    private final StatisticsService statisticsService;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceiveMessageDto message) {
        statisticsService.test(message);
        log.info("RECEIVED: {}", message.toString());
    }

    @Override
    public void send(SendMessageDto message) {

    }
}

