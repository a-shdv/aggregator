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

        habrParserService
                .findVacancies(
                        receiveMessageDto.getUsername(),
                        receiveMessageDto.getTitle(),
                        receiveMessageDto.getSalary(),
                        receiveMessageDto.isOnlyWithSalary(),
                        receiveMessageDto.getExperience(),
                        receiveMessageDto.getCityId(),
                        receiveMessageDto.isRemoteAvailable()
                );

        hhRuParserService
                .findVacancies(
                        receiveMessageDto.getUsername(),
                        receiveMessageDto.getTitle(),
                        receiveMessageDto.getSalary(),
                        receiveMessageDto.isOnlyWithSalary(),
                        receiveMessageDto.getExperience(),
                        receiveMessageDto.getCityId(),
                        receiveMessageDto.isRemoteAvailable()
                );
        rabotaRuParserService
                .findVacancies(
                        receiveMessageDto.getUsername(),
                        receiveMessageDto.getTitle(),
                        receiveMessageDto.getSalary(),
                        receiveMessageDto.isOnlyWithSalary(),
                        receiveMessageDto.getExperience(),
                        receiveMessageDto.getCityId(),
                        receiveMessageDto.isRemoteAvailable()
                );
    }
}
