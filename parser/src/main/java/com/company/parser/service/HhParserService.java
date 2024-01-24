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
@Slf4j
@RequiredArgsConstructor
public class HhParserService {
    private final RabbitMqSenderService rabbitMqSenderService;
    private static final int vacanciesPerPage = 20;

    public CompletableFuture<Void> findAllVacancies(String query, Integer amount, BigDecimal salary) {
        return CompletableFuture.runAsync(() -> {
            int currentPage = 0;
            int previousPage;
            StringBuilder url = new StringBuilder("https://hh.ru/search/vacancy" +
                    "?hhtmFrom=main" +
                    "&hhtmFromLabel=vacancy_search_line" +
                    "&search_field=name" +
                    "&search_field=company_name" +
                    "&search_field=description" +
                    "&enable_snippets=false" +
                    "&L_save_area=true" +
                    "&area=1" + // Москва
                    "&text=" + query +
                    "&page=" + currentPage +
                    "&salary=" + salary +
                    "&customDomain=1");

            Document doc = null;

            while (currentPage < amount / vacanciesPerPage) {
                try {
                    doc = Jsoup.connect(url.toString()).get();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                if (doc != null) {
                    final Elements elements = doc.getElementsByClass("vacancy-serp-content").first().getElementsByClass("serp-item");
                    for (Element element : elements) {
                        String vacancyUrl = element.getElementsByClass("bloko-link").first().absUrl("href");
                        SendMessageDto sendMessageDto = parseWebPage(vacancyUrl);
                        rabbitMqSenderService.send(sendMessageDto);
                    }

                    previousPage = currentPage;
                    currentPage++;
                    url.replace(
                            url.indexOf("?page=" + previousPage),
                            url.lastIndexOf("?page=" + previousPage),
                            "?page=" + currentPage
                    );
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
