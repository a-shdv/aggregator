package com.company.parser.service;

import com.company.parser.rabbitmq.dto.SendMessageDto;
import com.company.parser.rabbitmq.service.RabbitMqSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabrParserService {
    private final RabbitMqSenderService rabbitMqSenderService;
    private static final int vacanciesPerPage = 25;

    public CompletableFuture<Void> findAllVacancies(String query, int amount, BigDecimal salary, boolean withSalary) {
        return CompletableFuture.runAsync(() -> {
            int previousPage;
            int currentPage = 1;
            StringBuilder url = new StringBuilder("https://career.habr.com/vacancies" +
                    "?page=" + currentPage + "&q=" + query + "&salary=" + salary + "&with_salary="  + withSalary + "&type=all");

            Document doc = null;
            while (currentPage <= amount / vacanciesPerPage) {
                try {
                    doc = Jsoup.connect(url.toString()).get();
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

                        rabbitMqSenderService.send(sendMessageDto);
                    }
                    previousPage = currentPage;
                    currentPage++;

                    url.replace(
                            url.indexOf("?page=" + previousPage),
                            url.lastIndexOf("?page=" + previousPage),
                            "?page=" + currentPage
                            );

//                    url = "https://career.habr.com/vacancies?page=" + currentPage + "&q=" + query + "&type=all" + "&salary=" + salary;
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
}
