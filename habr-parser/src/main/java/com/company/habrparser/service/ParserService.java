package com.company.habrparser.service;

import com.company.habrparser.rabbitmq.dto.SendMessageDto;
import com.company.habrparser.rabbitmq.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ParserService {
    private final RabbitMqService rabbitMqService;

    @Autowired
    public ParserService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    //    @Scheduled(initialDelay = 2000, fixedDelay = 3_600_000)
    @EventListener(ApplicationReadyEvent.class)
    public void findAllVacancies() {
        String title = "java";
        final String url = "https://career.habr.com/vacancies?q=" + title + "&type=all";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (doc != null) {
            final Elements sections = doc.getElementsByClass("section-box");
            for (int i = 1; i < 26; i++) {
                SendMessageDto message = parseWebPage(
                        sections.get(i).getElementsByClass("vacancy-card__title-link").first().absUrl("href"),
                        sections.get(i).getElementsByClass("vacancy-card__title").text(),
                        sections.get(i).getElementsByClass("vacancy-card__date").text(),
                        sections.get(i).getElementsByClass("vacancy-card__salary").text(),
                        sections.get(i).getElementsByClass("vacancy-card__company-title").text(),
                        sections.get(i).getElementsByClass("vacancy-card__skills").first().text(),
                        sections.get(i).getElementsByClass("vacancy-card__meta").text());
                System.out.println();
                sendMessageToRabbit(message);
            }
        }
    }

    private SendMessageDto parseWebPage(String url, String title, String date, String salary, String company, String requirements, String schedule) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (doc != null) {
            final String description = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left section").get(1).text();
            return SendMessageDto.builder()
                    .title(title)
                    .date(date)
                    .salary(salary)
                    .company(company)
                    .requirements(requirements)
                    .description(description)
                    .schedule(schedule)
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
