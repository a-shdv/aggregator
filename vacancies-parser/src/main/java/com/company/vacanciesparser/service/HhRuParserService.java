package com.company.vacanciesparser.service;

import com.company.vacanciesparser.rabbitmq.dto.ReceiveMessageDto;
import com.company.vacanciesparser.rabbitmq.dto.SendMessageDto;
import com.company.vacanciesparser.rabbitmq.service.RabbitMqSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
// 50 вакансий на одной странице
public class HhRuParserService {
    @Value("${websites.hh.num-of-elements-per-page}")
    private Integer amount;
    private final RabbitMqSenderService rabbitMqSenderService;

    @Async("asyncExecutor")
    public Future<Void> findVacancies(ReceiveMessageDto receiveMessageDto) {
        System.out.println("hh: " + Thread.currentThread().getName());

        String username = receiveMessageDto.getUsername();
        String query = receiveMessageDto.getTitle();
        BigDecimal salary = receiveMessageDto.getSalary();
        Boolean onlyWithSalary = receiveMessageDto.getIsOnlyWithSalary();
        Integer experience = receiveMessageDto.getExperience();
        Integer cityId = receiveMessageDto.getCityId();
        Boolean isRemoteAvailable = receiveMessageDto.getIsRemoteAvailable();
        Integer numOfRequests = receiveMessageDto.getNumOfRequests();

        int currentPage = -1 + numOfRequests;
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
        Elements elements;
        if (doc != null) {
            elements = doc
                    .getElementsByClass("vacancy-serp-content").first() != null ?
                    doc
                            .getElementsByClass("vacancy-serp-content").first()
                            .getElementsByClass("serp-item") : null;
        } else {
            elements = null;
        }

        if (elements != null && !elements.isEmpty()) {
            final List<SendMessageDto> sendMessageDtoList = new ArrayList<>();

            while (currentPage < amount / elements.size()) {
                int sixteenElements = elements.size() / 3;
                CompletableFuture<?> firstThirdOfPage = CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < sixteenElements; i++) {
                        String vacancyUrl = elements.get(i).getElementsByClass("bloko-link").first().absUrl("href");
                        SendMessageDto sendMessageDto = parseVacancyWebPage(username, vacancyUrl);
                        sendMessageDtoList.add(sendMessageDto);
                    }
                }).thenRun(() -> {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                });

                CompletableFuture<?> secondThirdOfPage = CompletableFuture.runAsync(() -> {
                    for (int i = sixteenElements; i < sixteenElements + sixteenElements; i++) {
                        String vacancyUrl = elements.get(i).getElementsByClass("bloko-link").first().absUrl("href");
                        SendMessageDto sendMessageDto = parseVacancyWebPage(username, vacancyUrl);
                        sendMessageDtoList.add(sendMessageDto);
                    }
                }).thenRun(() -> {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                });

                CompletableFuture<?> thirdThirdOfPage = CompletableFuture.runAsync(() -> {
                    for (int i = sixteenElements + sixteenElements; i < elements.size(); i++) {
                        String vacancyUrl = elements.get(i).getElementsByClass("bloko-link").first().absUrl("href");
                        SendMessageDto sendMessageDto = parseVacancyWebPage(username, vacancyUrl);
                        sendMessageDtoList.add(sendMessageDto);
                    }
                }).thenRun(() -> {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                });

                CompletableFuture.allOf(firstThirdOfPage, secondThirdOfPage, thirdThirdOfPage).join();

                previousPage = currentPage;
                currentPage++;
                url.replace(
                        url.indexOf("&page=" + previousPage),
                        url.lastIndexOf("&page=" + previousPage),
                        "&page=" + currentPage
                );

//                if (sendMessageDtoList.size() == elements.size()) {
//                    rabbitMqSenderService.send(sendMessageDtoList);
//                    sendMessageDtoList.clear();
//                }
            }
            // Отправка оставшихся сообщений, если в списке осталось < elements.size() сообщений после парсинга
            if (!sendMessageDtoList.isEmpty()) {
                rabbitMqSenderService.send(sendMessageDtoList);
                sendMessageDtoList.clear();
            }
        } else {
            log.error("Could not parse elements: {}", this.getClass().getName());
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
            case 10 -> parsedCityId = 54; // Красноярск
            case 11 -> parsedCityId = 104; // Челябинск
            case 12 -> parsedCityId = 78; // Самара
            case 13 -> parsedCityId = 76; // Ростов-на-Дону
            case 14 -> parsedCityId = 53; // Краснодар
            case 15 -> parsedCityId = 68; // Омск
            case 16 -> parsedCityId = 26; // Воронеж
            case 17 -> parsedCityId = 72; // Пермь
            case 18 -> parsedCityId = 24; // Волгоград
            case 20 -> parsedCityId = 79; // Саратов
            case 21 -> parsedCityId = 95; // Тюмень
            case 23 -> parsedCityId = 11; // Барнаул
            case 24 -> parsedCityId = 29; // Махачкала
            case 25 -> parsedCityId = 96; // Ижевск
            case 26 -> parsedCityId = 102; // Хабаровск
            case 27 -> parsedCityId = 35; // Иркутск
            case 28 -> parsedCityId = 22; // Владивосток
            case 29 -> parsedCityId = 112; // Ярославль
            case 30 -> parsedCityId = 130; // Севастополь
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
