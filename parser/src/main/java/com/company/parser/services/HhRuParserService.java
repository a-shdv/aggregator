package com.company.parser.services;

import com.company.parser.rabbitmq.dtos.SendMessageDto;
import com.company.parser.rabbitmq.services.RabbitMqSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class HhRuParserService {
    private static final Integer amount = 60;
    private final RabbitMqSenderService rabbitMqSenderService;

    @Async
    public CompletableFuture<Void> findVacancies(String username, String query, BigDecimal salary, Boolean onlyWithSalary,
                                                 Integer experience, Integer cityId, Boolean isRemoteAvailable) {
        int currentPage = 0;
        int previousPage;

        String schedule = isRemoteAvailable ? "&schedule=remote" : "";
        StringBuilder url = new StringBuilder("https://hh.ru/search/vacancy" +
                "?hhtmFrom=main" +
                "&hhtmFromLabel=vacancy_search_line" +
                "&search_field=name" +
                "&search_field=company_name" +
                "&search_field=description" +
                "&enable_snippets=false" +
                "&L_save_area=true" +
                "&area=" + parseCityId(cityId) +
                "&text=" + query +
                "&page=" + currentPage +
                "&salary=" + salary +
                "&only_with_salary=" + onlyWithSalary +
                "&experience=" + parseExperience(experience) +
                "&schedule=fullDay" + schedule +
                "&customDomain=1");

        Document doc = connectDocumentToUrl(url.toString());
        Elements elements = null;
        if (doc != null) {
            elements = doc
                    .getElementsByClass("vacancy-serp-content").first() != null ?
                    doc
                            .getElementsByClass("vacancy-serp-content").first()
                            .getElementsByClass("serp-item") : null;
        }

        if (elements != null && !elements.isEmpty()) {
            final List<SendMessageDto> sendMessageDtoList = new ArrayList<>();

            while (currentPage < amount / elements.size()) {
                elements.forEach(element -> {
                    String vacancyUrl = element.getElementsByClass("bloko-link").first().absUrl("href");
                    SendMessageDto sendMessageDto = parseVacancyWebPage(username, vacancyUrl);
                    sendMessageDtoList.add(sendMessageDto);
                });

                previousPage = currentPage;
                currentPage++;
                url.replace(
                        url.indexOf("&page=" + previousPage),
                        url.lastIndexOf("&page=" + previousPage),
                        "&page=" + currentPage
                );

                if (sendMessageDtoList.size() == elements.size()) {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                }
            }
            // Отправка оставшихся сообщений, если в списке осталось < elements.size() сообщений после парсинга
            if (!sendMessageDtoList.isEmpty()) {
                rabbitMqSenderService.send(sendMessageDtoList);
                sendMessageDtoList.clear();
            }
        } else {
            log.error("Could not parse elements");
        }
        return CompletableFuture.completedFuture(null);
    }

    private int parseCityId(int cityId) {
        int parsedCityId = 0;
        switch (cityId) {
            case 0 -> parsedCityId = 1; // Москва
            case 1 -> parsedCityId = 2; // СПБ
            case 2 -> parsedCityId = 3; // ЕКБ
            case 3 -> parsedCityId = 4; // Новосибирск
            case 4 -> parsedCityId = 88; // Казань
            case 5 -> parsedCityId = 66; // Нижний Новгород
            case 6 -> parsedCityId = 98; // Ульяновск
            case 7 -> parsedCityId = 212; // Тольятти
            case 8 -> parsedCityId = 15; // Астрахань
            case 9 -> parsedCityId = 99; // Уфа
        }
        return parsedCityId;
    }

    private String parseExperience(int experience) {
        String parsedExperience = "doesNotMatter";
        switch (experience) {
            case 0 -> parsedExperience = "doesNotMatter"; // 0 - не имеет значения
            case 1 -> parsedExperience = "noExperience"; // 1 - нет опыта
            case 2 -> parsedExperience = "between1And3"; // 2 - от 1 года до 3 лет
            case 3 -> parsedExperience = "between3And6"; // 3 - от 3 до 6 лет
            case 4 -> parsedExperience = "moreThan6";// 4 - более 6 лет
        }
        return parsedExperience;
    }

    private SendMessageDto parseVacancyWebPage(String username, String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (doc != null) {
            return SendMessageDto.builder()
                    .username(username)
                    .title(doc.getElementsByClass("vacancy-title").first().getElementsByClass("bloko-header-section-1").text())
                    .salary(doc.getElementsByClass("vacancy-title").first().getElementsByTag("span").text())
                    .company(doc.getElementsByClass("vacancy-company-details").first().getElementsByClass("vacancy-company-name").text())
                    .requirements(doc.getElementsByClass("bloko-tag-list").text())
                    .description(doc.getElementsByClass("vacancy-section").first().getElementsByAttribute("data-qa").first().text())
                    .schedule(doc.getElementsByClass("vacancy-description-list-item").text())
                    .date(doc.getElementsByClass("vacancy-creation-time-redesigned").text())
                    .logo(doc.getElementsByClass("vacancy-company-logo-image-redesigned").first() != null ? doc.getElementsByClass("vacancy-company-logo-image-redesigned").first().absUrl("src") : null)
                    .source(url)
                    .build();
        }
        return null;
    }

    private Document connectDocumentToUrl(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
