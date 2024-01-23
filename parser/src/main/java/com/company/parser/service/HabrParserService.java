package com.company.parser.service;

import com.company.parser.rabbitmq.dto.SendMessageDto;
import com.company.parser.rabbitmq.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabrParserService {
    private final RabbitMqService rabbitMqService;

    @EventListener(ApplicationReadyEvent.class)
    public void findAllVacancies() {
        CompletableFuture.runAsync(() -> {
            System.out.println("Current Thread: " + Thread.currentThread().getName());
            String query = "java";
            int page = 1;
            final String url = "https://career.habr.com/vacancies?page=" + page + "&q=" + query + "&type=all";
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            if (doc != null) {
                Elements sections = doc.getElementsByClass("section-group section-group--gap-medium").last().getElementsByClass("section-box");
                for (Element section : sections) {
                    String vacancyUrl = section.getElementsByClass("vacancy-card__title-link").first().absUrl("href");

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
