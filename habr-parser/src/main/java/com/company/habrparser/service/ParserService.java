package com.company.habrparser.service;

import com.company.habrparser.rabbitmq.dto.SendMessageDto;
import com.company.habrparser.rabbitmq.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ParserService {
    private final RabbitMqService rabbitMqService;

    @Autowired
    public ParserService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void findAllVacancies() {
        CompletableFuture.runAsync(() -> {
            System.out.println("Current Thread: " + Thread.currentThread().getName());
            String query = "java";
            final String url = "https://career.habr.com/vacancies?q=" + query + "&type=all";
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            if (doc != null) {
                final Elements sections = doc.getElementsByClass("section-box");
                for (Element section : sections) {
                    final String vacancyUrl = section.getElementsByClass("vacancy-card__title-link").first().absUrl("href");

                    SendMessageDto sendMessageDto = SendMessageDto.builder()
                            .title(section.getElementsByClass("vacancy-card__title").text())
                            .date(section.getElementsByClass("vacancy-card__date").text())
                            .salary(section.getElementsByClass("vacancy-card__salary").text())
                            .company(section.getElementsByClass("vacancy-card__company-title").text())
                            .requirements(section.getElementsByClass("vacancy-card__skills").first().text())
                            .schedule(section.getElementsByClass("vacancy-card__meta").text())
                            .description(parseWebPageDescription(vacancyUrl))
                            .source(vacancyUrl)
                            .build();

                    sendMessageToRabbit(sendMessageDto);
                }
            }
        });

    }

    private String parseWebPageDescription(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (doc != null) {
            return doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left section").get(1).text();
        }
        return null;
    }

    private void sendMessageToRabbit(SendMessageDto sendMessageDto) {
        rabbitMqService.send(sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}
