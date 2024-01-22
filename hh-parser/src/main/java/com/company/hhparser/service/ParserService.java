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
        final String url = "https://ulyanovsk.hh.ru/search/vacancy?text=" + title + "&area=98&hhtmFrom=main&hhtmFromLabel=vacancy_search_line";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (doc != null) {
            final Elements sections = doc.getElementsByClass("serp-item");
//            final Elements sections = doc.getElementsByClass("sticky-sidebar-and-content--NmOyAQ7IxIOkgRiBRSEg");
            for (int i = 0; i < sections.size(); i++) {
                var source = sections.get(i).getElementsByClass("bloko-link").first().absUrl("href");
                var vacTitle = sections.get(i).getElementsByClass("serp-item__title").text();
                var company = sections.get(i).getElementsByClass("vacancy-serp-item__meta-info-company").text();
                SendMessageDto sendMessageDto = parseWebPage(source);
                /*                SendMessageDto dto = SendMessageDto.builder()
                        .title(sections.first().getElementsByClass("serp-item__title").get(i).text())
//                        .date()
//                        .salary()
                        .company(sections.first().getElementsByClass("vacancy-serp-item__meta-info-company").get(i).getElementsByClass("bloko-link bloko-link_kind-tertiary").text())
//                        .requirements()
//                        .description()
//                        .schedule()
//                        .source()
                        .build();*/


            }
        }
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
//            final String description = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left section").get(1).text();
//            return SendMessageDto.builder()
//                    .title(title)
//                    .date(date)
//                    .salary(salary)
//                    .company(company)
//                    .requirements(requirements)
//                    .description(description)
//                    .schedule(schedule)
//                    .source(url)
//                    .build();
        }
        return null;
    }

    private void sendMessageToRabbit(SendMessageDto sendMessageDto) {
        rabbitMqService.send(sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}
