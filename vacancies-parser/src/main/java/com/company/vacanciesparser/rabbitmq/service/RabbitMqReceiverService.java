package com.company.vacanciesparser.rabbitmq.service;

import com.company.vacanciesparser.rabbitmq.dto.CancelParsingDto;
import com.company.vacanciesparser.rabbitmq.dto.ReceiveMessageDto;
import com.company.vacanciesparser.service.HabrParserService;
import com.company.vacanciesparser.service.HhRuParserService;
import com.company.vacanciesparser.service.RabotaRuParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Set;
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
            habrParserService.findVacancies(receiveMessageDto);
            hhRuParserService.findVacancies(receiveMessageDto);
            rabotaRuParserService.findVacancies(receiveMessageDto);
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
