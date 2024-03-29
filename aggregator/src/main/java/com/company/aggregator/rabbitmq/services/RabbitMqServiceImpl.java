package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.services.impl.StatisticsServiceImpl;
import com.company.aggregator.services.impl.UserServiceImpl;
import com.company.aggregator.services.impl.VacancyServiceImpl;
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
    private final VacancyServiceImpl vacancyServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final StatisticsServiceImpl statisticsServiceImpl;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive0}")
    public void receiveVacanciesParser(List<com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto> receiveMessageDtoList) {
        log.info("RECEIVED: {}", receiveMessageDtoList);
        User user = userServiceImpl.findUserByUsername(receiveMessageDtoList.get(0).getUsername());
        if (!receiveMessageDtoList.isEmpty()) {
            vacancyServiceImpl.saveVacancies(receiveMessageDtoList, user);
        }
        progressbarLoaderCounter += receiveMessageDtoList.size();
        messageSendingOperations.convertAndSend("/topic/public", WebSocketSendMessageDto.builder()
                .content(String.valueOf(8.4 * progressbarLoaderCounter)) // 100 / 12 (кол-во элементов на одной странице) = 1%
                .type("RECEIVE").build());
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue-to-receive1}")
    public void receiveStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message) {
        log.info("RECEIVED: {}", message);
        if (message.getUsername() != null) {
            User user = userServiceImpl.findUserByUsername(message.getUsername());
            statisticsServiceImpl.deleteStatisticsByUser(user);
            statisticsServiceImpl.saveStatistics(user, message);
        }
    }

    @Override
    public void sendToVacanciesParser(com.company.aggregator.rabbitmq.dtos.vacancies.SendMessageDto sendMessageDto) {
        vacancyServiceImpl.deleteVacanciesByUser(userServiceImpl.findUserByUsername(sendMessageDto.getUsername()));
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
}

