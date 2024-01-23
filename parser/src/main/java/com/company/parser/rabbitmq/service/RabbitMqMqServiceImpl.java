package com.company.parser.rabbitmq.service;

import com.company.parser.rabbitmq.dto.ReceiveMessageDto;
import com.company.parser.rabbitmq.dto.SendMessageDto;
import com.company.parser.rabbitmq.properties.RabbitMqProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMqMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;

    @Override
    public void receive(ReceiveMessageDto message) {

    }

    @Override
    public void send(SendMessageDto message) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), message);
    }

/*    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceivedMessageDto message) {
        sheetCutterService.processCommand(message);
    }*/

/*    @Override
    public void send(MessageDto message) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), message);
    }*/

}
