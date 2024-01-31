package com.company.aggregator.rabbitmq.service;


import com.company.aggregator.model.User;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dto.SendMessageDto;
import com.company.aggregator.rabbitmq.property.RabbitMqProperties;
import com.company.aggregator.service.AggregatorService;
import com.company.aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;
    private final AggregatorService aggregatorService;
    private final UserService userService;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(List<ReceiveMessageDto> receiveMessageDtoList) {
        User user = userService.findUserByUsername(receiveMessageDtoList.get(0).getUsername());
        log.info("RECEIVED: {}", receiveMessageDtoList.toString());
        receiveMessageDtoList
                .removeIf(receiveMessageDto -> aggregatorService.findBySource(receiveMessageDto.getSource()) != null);
        if (!receiveMessageDtoList.isEmpty()) {
            aggregatorService.saveMessageListAsync(receiveMessageDtoList, user);
        }
    }

    @Override
    public void send(SendMessageDto sendMessageDto) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}

