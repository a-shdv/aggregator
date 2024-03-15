package com.company.parser.rabbitmq.services;

import com.company.parser.rabbitmq.dtos.ReceiveMessageDto;
import com.company.parser.services.HabrParserService;
import com.company.parser.services.HhRuParserService;
import com.company.parser.services.RabotaRuParserService;
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
            CompletableFuture<Void> habr = habrParserService
                    .findVacancies(
                            receiveMessageDto.getUsername(),
                            receiveMessageDto.getTitle(),
                            receiveMessageDto.getSalary(),
                            receiveMessageDto.getIsOnlyWithSalary(),
                            receiveMessageDto.getExperience(),
                            receiveMessageDto.getCityId(),
                            receiveMessageDto.getIsRemoteAvailable()
                    );

            CompletableFuture<Void> hh = hhRuParserService
                    .findVacancies(
                            receiveMessageDto.getUsername(),
                            receiveMessageDto.getTitle(),
                            receiveMessageDto.getSalary(),
                            receiveMessageDto.getIsOnlyWithSalary(),
                            receiveMessageDto.getExperience(),
                            receiveMessageDto.getCityId(),
                            receiveMessageDto.getIsRemoteAvailable()
                    );
            CompletableFuture<Void> rabota = rabotaRuParserService
                    .findVacancies(
                            receiveMessageDto.getUsername(),
                            receiveMessageDto.getTitle(),
                            receiveMessageDto.getSalary(),
                            receiveMessageDto.getIsOnlyWithSalary(),
                            receiveMessageDto.getExperience(),
                            receiveMessageDto.getCityId(),
                            receiveMessageDto.getIsRemoteAvailable()
                    );

            CompletableFuture.allOf(habr, hh, rabota);
    }
}
