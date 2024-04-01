package com.company.vacanciesparser.rabbitmq.services;

import com.company.vacanciesparser.rabbitmq.dtos.CancelParsingDto;
import com.company.vacanciesparser.rabbitmq.dtos.ReceiveMessageDto;
import com.company.vacanciesparser.services.HabrParserService;
import com.company.vacanciesparser.services.HhRuParserService;
import com.company.vacanciesparser.services.RabotaRuParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableRabbit
@Slf4j
public class RabbitMqReceiverService {
    private final HabrParserService habrParserService;
    private final HhRuParserService hhRuParserService;
    private final RabotaRuParserService rabotaRuParserService;

    @RabbitListener(queues = "${rabbitmq.queue-to-receive0}")
    public void receive(ReceiveMessageDto receiveMessageDto) {
        log.info("RECEIVED: {}", receiveMessageDto.toString());
        if (receiveMessageDto.getUsername() != null) {
            CompletableFuture.anyOf(
                    habrParserService
                            .findVacancies(
                                    receiveMessageDto.getUsername(),
                                    receiveMessageDto.getTitle(),
                                    receiveMessageDto.getSalary(),
                                    receiveMessageDto.getIsOnlyWithSalary(),
                                    receiveMessageDto.getExperience(),
                                    receiveMessageDto.getCityId(),
                                    receiveMessageDto.getIsRemoteAvailable(),
                                    receiveMessageDto.getNumOfRequests()
                            ),

                    hhRuParserService
                            .findVacancies(
                                    receiveMessageDto.getUsername(),
                                    receiveMessageDto.getTitle(),
                                    receiveMessageDto.getSalary(),
                                    receiveMessageDto.getIsOnlyWithSalary(),
                                    receiveMessageDto.getExperience(),
                                    receiveMessageDto.getCityId(),
                                    receiveMessageDto.getIsRemoteAvailable(),
                                    receiveMessageDto.getNumOfRequests()
                            ),
                    rabotaRuParserService
                            .findVacancies(
                                    receiveMessageDto.getUsername(),
                                    receiveMessageDto.getTitle(),
                                    receiveMessageDto.getSalary(),
                                    receiveMessageDto.getIsOnlyWithSalary(),
                                    receiveMessageDto.getExperience(),
                                    receiveMessageDto.getCityId(),
                                    receiveMessageDto.getIsRemoteAvailable(),
                                    receiveMessageDto.getNumOfRequests()
                            )).join();
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue-to-receive1}")
    public void receiveCancel(CancelParsingDto cancelParsingDto) {
        log.info("RECEIVED: {}", cancelParsingDto.toString());
        if (cancelParsingDto.getIsParsingCancelled()) {
            Set<Thread> threads = Thread.getAllStackTraces().keySet().stream().filter((thread) -> thread.getName().startsWith("AsyncThread-")).collect(Collectors.toSet());
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
    }
}
