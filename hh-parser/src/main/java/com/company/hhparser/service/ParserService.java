package com.company.hhparser.service;

import com.company.hhparser.rabbitmq.dto.SendMessageDto;
import com.company.hhparser.rabbitmq.service.RabbitMqService;
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
            System.out.println("Current Thread: " + Thread.currentThread());
            String query = "developer";
            int page = 1;
            final String url = "https://hh.ru/search/vacancy?text=" + query + "&area=98&hhtmFrom=main&hhtmFromLabel=vacancy_search_line&page=" + page;
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            if (doc != null) {
                final Elements sections = doc.getElementsByClass("serp-item");
                for (Element section : sections) {
                    String vacancyUrl = section.getElementsByClass("bloko-link").first().absUrl("href");
                    SendMessageDto sendMessageDto = parseWebPage(vacancyUrl);
                    sendMessageToRabbit(sendMessageDto);
                }
            }
        });
    }

    private SendMessageDto parseWebPage(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (doc != null) {
            return SendMessageDto.builder()
                    .title(doc.getElementsByClass("vacancy-title").first().getElementsByClass("bloko-header-section-1").text())
                    .salary(doc.getElementsByClass("vacancy-title").first().getElementsByTag("span").text())
                    .company(doc.getElementsByClass("vacancy-company-details").first().getElementsByClass("vacancy-company-name").text())
                    .requirements(doc.getElementsByClass("bloko-tag-list").text())
                    .description(doc.getElementsByClass("vacancy-section").first().getElementsByAttribute("data-qa").first().text())
                    .schedule(doc.getElementsByClass("vacancy-description-list-item").text())
                    .date(doc.getElementsByClass("vacancy-creation-time-redesigned").text())
                    .source(url)
                    .build();
        }
        return null;
    }

    private void sendMessageToRabbit(SendMessageDto sendMessageDto) {
        rabbitMqService.send(sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}
