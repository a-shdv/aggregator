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
        System.out.println(Thread.currentThread().getName());
        if (receiveMessageDto.getUsername() != null) {
            CompletableFuture<Void> habr = habrParserService.findVacancies(receiveMessageDto);
            CompletableFuture<Void> hh = hhRuParserService.findVacancies(receiveMessageDto);
            CompletableFuture<Void> rabota = rabotaRuParserService.findVacancies(receiveMessageDto);

            habr.thenRun(() -> System.out.println("habr completed!"));
            hh.thenRun(() -> System.out.println("hh completed!"));
            rabota.thenRun(() -> System.out.println("rabota completed!"));

            CompletableFuture<Void> allTasks = CompletableFuture.allOf(habr, hh, rabota);
            allTasks.thenRun(() -> System.out.println("ALL TASKS COMPLETED SUCCESSFULLY"));
            System.out.println("-----------------------------------------");
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
