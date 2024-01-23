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
@Slf4j
@RequiredArgsConstructor
public class HhParserService {
    private final RabbitMqService rabbitMqService;

    @EventListener(ApplicationReadyEvent.class)
    public CompletableFuture<Void> findAllVacancies() {
        return CompletableFuture.runAsync(() -> {
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
                    rabbitMqService.send(sendMessageDto);
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
}
