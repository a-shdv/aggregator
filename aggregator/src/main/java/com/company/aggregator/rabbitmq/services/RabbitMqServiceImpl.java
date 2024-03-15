package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.dtos.WebsocketDto;
import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.services.UserService;
import com.company.aggregator.services.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRabbit
public class RabbitMqServiceImpl implements RabbitMqService {

    private static int previousProgressbarLoaderCounter = 0;
    private static int progressbarLoaderCounter = 0;
    private static boolean previousProgressbarLoaderCounterIsSet = false;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitProperties;
    private final VacancyService vacancyService;
    private final UserService userService;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(List<ReceiveMessageDto> receiveMessageDtoList) {
        User user = userService.findUserByUsername(receiveMessageDtoList.get(0).getUsername());
        log.info("RECEIVED: {}", receiveMessageDtoList);
//        receiveMessageDtoList
//                .removeIf(receiveMessageDto -> aggregatorService.findBySource(receiveMessageDto.getSource()) != null);
        if (!receiveMessageDtoList.isEmpty()) {
            vacancyService.saveMessageListAsync(receiveMessageDtoList, user);
        }
        progressbarLoaderCounter += receiveMessageDtoList.size();
        messageSendingOperations.convertAndSend("/topic/public", WebsocketDto.builder().counter(progressbarLoaderCounter).type("RECEIVE").build());
    }

    @Override
    public void send(SendMessageDto sendMessageDto) {
        vacancyService.deleteVacanciesByUserAsync(userService.findUserByUsername(sendMessageDto.getUsername()));
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }

    @Scheduled(initialDelay = 2_000, fixedDelay = 10_000)
    public void checkProgressbarLoaderCounter() {
        if (previousProgressbarLoaderCounter == progressbarLoaderCounter) {
            progressbarLoaderCounter = 0;
            messageSendingOperations.convertAndSend("/topic/public", WebsocketDto.builder().counter(100).type("RECEIVE").build());
        }
        previousProgressbarLoaderCounter = progressbarLoaderCounter;

    }
}

