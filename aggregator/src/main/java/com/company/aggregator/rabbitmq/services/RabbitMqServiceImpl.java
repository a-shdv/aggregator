package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.services.StatisticsService;
import com.company.aggregator.services.UserService;
import com.company.aggregator.services.VacancyService;
import com.company.aggregator.websockets.dtos.WebSocketSendMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
    private final StatisticsService statisticsService;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive0}")
    public void receiveVacanciesParser(List<com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto> receiveMessageDtoList) {
        log.info("RECEIVED: {}", receiveMessageDtoList);
        User user = userService.findUserByUsername(receiveMessageDtoList.get(0).getUsername());
        if (!receiveMessageDtoList.isEmpty()) {
            vacancyService.saveMessageList(receiveMessageDtoList, user);
        }
        progressbarLoaderCounter += receiveMessageDtoList.size();
        messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content(String.valueOf(progressbarLoaderCounter)).type("RECEIVE").build());
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive1}")
    public void receiveStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message) {
        log.info("RECEIVED: {}", message);
        User user = userService.findUserByUsername(message.getUsername());
        user.setStatistics(com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto.toStatistics(message));
        userService.saveUserAsync(user);
//        statisticsService.saveStatisticsAsync(user, message).join();
    }

    @Override
    public void sendToVacanciesParser(com.company.aggregator.rabbitmq.dtos.vacancies.SendMessageDto sendMessageDto) {
        vacancyService.deleteVacanciesByUserAsync(userService.findUserByUsername(sendMessageDto.getUsername()));
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend0(), sendMessageDto);
        progressbarLoaderCounter = 0;
        messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content(String.valueOf(progressbarLoaderCounter)).type("RECEIVE").build());
        log.info("SENT (vacancies): {}", sendMessageDto);
    }

    @Override
    public void sendToStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.SendMessageDto sendMessageDto) {
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend1(), sendMessageDto);
        log.info("SENT (statistics): {}", sendMessageDto);
    }

//    @Scheduled(initialDelay = 15_000, fixedDelay = 10_000)
//    public void checkProgressbarLoaderCounter() {
//        if (previousProgressbarLoaderCounter == progressbarLoaderCounter ) {
//            progressbarLoaderCounter = 0;
//            messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder().content(String.valueOf(100)).type("RECEIVE").build());
//        }
//        previousProgressbarLoaderCounter = progressbarLoaderCounter;
//    }
}

