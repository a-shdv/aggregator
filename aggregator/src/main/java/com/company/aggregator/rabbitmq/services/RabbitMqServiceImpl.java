package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.services.VacancyService;
import com.company.aggregator.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMqServiceImpl implements RabbitMqService {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitMqProperties rabbitProperties;
    private final VacancyService vacancyService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private static int progressbarLoaderCounter = 0;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive}", id = "jh-queue-to-aggregator-id")
    @SendTo("/topic/progressbar")
    public void receive(List<ReceiveMessageDto> receiveMessageDtoList) {
        User user = userService.findUserByUsername(receiveMessageDtoList.get(0).getUsername());
        log.info("RECEIVED: {}", receiveMessageDtoList);
//        receiveMessageDtoList
//                .removeIf(receiveMessageDto -> aggregatorService.findBySource(receiveMessageDto.getSource()) != null);
        if (!receiveMessageDtoList.isEmpty()) {
            vacancyService.saveMessageListAsync(receiveMessageDtoList, user);
        }

        progressbarLoaderCounter += 12;
        messagingTemplate.convertAndSend("/topic/progressbar", progressbarLoaderCounter);
    }

    @Override
    public void send(SendMessageDto sendMessageDto) {
        vacancyService.deleteVacanciesByUserAsync(userService.findUserByUsername(sendMessageDto.getUsername()));
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}

