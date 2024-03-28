package com.company.vacanciesparser.rabbitmq.services;

import com.company.vacanciesparser.rabbitmq.dtos.ReceiveMessageDto;
import com.company.vacanciesparser.services.HabrParserService;
import com.company.vacanciesparser.services.HhRuParserService;
import com.company.vacanciesparser.services.RabotaRuParserService;
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
    private final HabrParserService habrParserService;
    private final HhRuParserService hhRuParserService;
    private final RabotaRuParserService rabotaRuParserService;

    @RabbitListener(queues = "${rabbitmq.queue-to-receive}")
    public void receive(ReceiveMessageDto receiveMessageDto) {
        log.info("RECEIVED: {}", receiveMessageDto.toString());

        CompletableFuture.anyOf(
                habrParserService
                        .findVacancies(
                                receiveMessageDto.getUsername(),
                                receiveMessageDto.getTitle(),
                                receiveMessageDto.getSalary(),
                                receiveMessageDto.getIsOnlyWithSalary(),
                                receiveMessageDto.getExperience(),
                                receiveMessageDto.getCityId(),
                                receiveMessageDto.getIsRemoteAvailable()
                        ),

                hhRuParserService
                        .findVacancies(
                                receiveMessageDto.getUsername(),
                                receiveMessageDto.getTitle(),
                                receiveMessageDto.getSalary(),
                                receiveMessageDto.getIsOnlyWithSalary(),
                                receiveMessageDto.getExperience(),
                                receiveMessageDto.getCityId(),
                                receiveMessageDto.getIsRemoteAvailable()
                        ),
                rabotaRuParserService
                        .findVacancies(
                                receiveMessageDto.getUsername(),
                                receiveMessageDto.getTitle(),
                                receiveMessageDto.getSalary(),
                                receiveMessageDto.getIsOnlyWithSalary(),
                                receiveMessageDto.getExperience(),
                                receiveMessageDto.getCityId(),
                                receiveMessageDto.getIsRemoteAvailable()
                        )).join();
    }
}
