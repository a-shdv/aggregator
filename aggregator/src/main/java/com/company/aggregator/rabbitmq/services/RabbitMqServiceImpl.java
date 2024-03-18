package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.services.UserService;
import com.company.aggregator.services.VacancyService;
import com.company.aggregator.websockets.dtos.WebSocketSendMessageDto;
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
        messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content(String.valueOf(progressbarLoaderCounter)).type("RECEIVE").build());
    }

    @Override
    public void sendToVacanciesParser(com.company.aggregator.rabbitmq.dtos.vacancies.SendMessageDto sendMessageDto) {
        vacancyService.deleteVacanciesByUserAsync(userService.findUserByUsername(sendMessageDto.getUsername()));
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend0(), sendMessageDto);
        messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content(String.valueOf(0)).type("RECEIVE").build());
        log.info("SENT (vacancies): {}", sendMessageDto);
    }

    @Override
    public void sendToStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.SendMessageDto sendMessageDto) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend1(), sendMessageDto);
        log.info("SENT (statistics): {}", sendMessageDto);
    }

    @Scheduled(initialDelay = 15_000, fixedDelay = 10_000)
    public void checkProgressbarLoaderCounter() {
        if (previousProgressbarLoaderCounter == progressbarLoaderCounter ) {
            progressbarLoaderCounter = 0;
            messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content(String.valueOf(100)).type("RECEIVE").build());
        }
        previousProgressbarLoaderCounter = progressbarLoaderCounter;
    }
}

