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
@RequiredArgsConstructor
@Slf4j
// 25 вакансий на одной странице
public class HabrParserService {

    @Value("${websites.habr.num-of-elements-per-page}")
    private Integer amount;
    private final RabbitMqSenderService rabbitMqSenderService;

    @Async("asyncExecutor")
    public CompletableFuture<Void> findVacancies(ReceiveMessageDto receiveMessageDto) {
        System.out.println("habr: " +Thread.currentThread().getName());

        String username = receiveMessageDto.getUsername();
        String query = receiveMessageDto.getTitle();
        BigDecimal salary = receiveMessageDto.getSalary();
        Boolean onlyWithSalary = receiveMessageDto.getIsOnlyWithSalary();
        Integer experience = receiveMessageDto.getExperience();
        Integer cityId = receiveMessageDto.getCityId();
        Boolean isRemoteAvailable = receiveMessageDto.getIsRemoteAvailable();
        Integer numOfRequests = receiveMessageDto.getNumOfRequests();

        int previousPage;
        int currentPage = numOfRequests;

        if (cityId == null) {
            cityId = 0;
        }

        StringBuilder url = new StringBuilder("https://career.habr.com/vacancies" +
                "?page=" + currentPage + "&q=" + query + "&salary=" + salary + "&with_salary=" + onlyWithSalary +
                "&city_id=" + parseCityId(cityId) + "&remote=" + isRemoteAvailable + "&type=all");

        int parsedExperience = parseExperience(experience);
        if (parsedExperience != -1) {
            url.append("&qid=").append(parsedExperience);
        }

        Document doc = connectDocumentToUrl(url.toString());
        Elements elements;
        if (doc != null) {
            elements = doc
                    .getElementsByClass("section-group section-group--gap-medium").last()
                    .getElementsByClass("section-box");
        } else {
            elements = null;
        }

        if (elements != null && !elements.isEmpty()) {
            List<SendMessageDto> sendMessageDtoList = new ArrayList<>();

            while (currentPage <= amount / elements.size()) {

                CompletableFuture<?> firstHalfOfPage = CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < elements.size() / 2; i++) {
                        String vacancyUrl = elements
                                .get(i)
                                .getElementsByClass("vacancy-card__title-link").first()
                                .absUrl("href");

                        SendMessageDto sendMessageDto = SendMessageDto.builder()
                                .username(username)
                                .title(elements.get(i).getElementsByClass("vacancy-card__title").text())
                                .date(elements.get(i).getElementsByClass("vacancy-card__date").text())
                                .salary(elements.get(i).getElementsByClass("vacancy-card__salary").text())
                                .company(elements.get(i).getElementsByClass("vacancy-card__company-title").text())
                                .requirements(elements.get(i).getElementsByClass("vacancy-card__skills").first().text())
                                .schedule(elements.get(i).getElementsByClass("vacancy-card__meta").text())
                                .description(parseWebPageDescription(vacancyUrl))
                                .source(vacancyUrl)
                                .logo(doc.getElementsByClass("vacancy-card__icon").get(i) != null ? doc.getElementsByClass("vacancy-card__icon").get(i).absUrl("src") : null)
                                .build();

                        sendMessageDtoList.add(sendMessageDto);
                    }
                }).thenRun(() -> {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                });

                CompletableFuture<?> secondsHalfOfPage = CompletableFuture.runAsync(() -> {
                    for (int i = elements.size() / 2; i < elements.size(); i++) {
                        String vacancyUrl = elements
                                .get(i)
                                .getElementsByClass("vacancy-card__title-link").first()
                                .absUrl("href");

                        SendMessageDto sendMessageDto = SendMessageDto.builder()
                                .username(username)
                                .title(elements.get(i).getElementsByClass("vacancy-card__title").text())
                                .date(elements.get(i).getElementsByClass("vacancy-card__date").text())
                                .salary(elements.get(i).getElementsByClass("vacancy-card__salary").text())
                                .company(elements.get(i).getElementsByClass("vacancy-card__company-title").text())
                                .requirements(elements.get(i).getElementsByClass("vacancy-card__skills").first().text())
                                .schedule(elements.get(i).getElementsByClass("vacancy-card__meta").text())
                                .description(parseWebPageDescription(vacancyUrl))
                                .source(vacancyUrl)
                                .logo(doc.getElementsByClass("vacancy-card__icon").get(i) != null ? doc.getElementsByClass("vacancy-card__icon").get(i).absUrl("src") : null)
                                .build();

                        sendMessageDtoList.add(sendMessageDto);
                    }
                }).thenRun(() -> {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                });

                CompletableFuture.allOf(firstHalfOfPage, secondsHalfOfPage).join();



                previousPage = currentPage;
                currentPage++;

                url.replace(
                        url.indexOf("?page=" + previousPage),
                        url.lastIndexOf("?page=" + previousPage),
                        "?page=" + currentPage
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
            case 0 -> parsedCityId = 678; // Москва
            case 1 -> parsedCityId = 679; // СПБ
            case 2 -> parsedCityId = 693; // ЕКБ
            case 3 -> parsedCityId = 717; // Новосибирск
            case 4 -> parsedCityId = 698; // Казань
            case 5 -> parsedCityId = 715; // Нижний Новгород
            case 6 -> parsedCityId = 739; // Ульяновск
            case 7 -> parsedCityId = 735; // Тольятти
            case 8 -> parsedCityId = 683; // Астрахань
            case 9 -> parsedCityId = 740; // Уфа
            case 10 -> parsedCityId = 708; // Красноярск
            case 11-> parsedCityId = 744; // Челябинск
            case 12 -> parsedCityId = 728; // Самара
            case 13 -> parsedCityId = 726; // Ростов-на-Дону
            case 14 -> parsedCityId = 707; // Краснодар
            case 15 -> parsedCityId = 718; // Омск
            case 16 -> parsedCityId = 692; // Воронеж
            case 17 -> parsedCityId = 722; // Пермь
            case 18 -> parsedCityId = 690; // Волгоград
            case 20 -> parsedCityId = 729; // Саратов
            case 21 -> parsedCityId = 738; // Тюмень
            case 23 -> parsedCityId = 684; // Барнаул
            case 24 -> parsedCityId = 3346; // Махачкала
            case 25 -> parsedCityId = 695; // Ижевск
            case 26 -> parsedCityId = 741; // Хабаровск
            case 27 -> parsedCityId = 696; // Иркутск
            case 28 -> parsedCityId = 688; // Владивосток
            case 29 -> parsedCityId = 747; // Ярославль
            case 30 -> parsedCityId = 914; // Севастополь
        }
        return parsedCityId;
    }

    private int parseExperience(int experience) {
        int parsedExperience = 0;
        switch (experience) {
            case 0 -> parsedExperience = -1; // -1 - не имеет значения
            case 1 -> parsedExperience = 1; // 1 - нет опыта
            case 2 -> parsedExperience = 3; // 3 - от 1 года до 3 лет
            case 3 -> parsedExperience = 4; // 4 - от 3 до 6 лет
            case 4 -> parsedExperience = 5;// 5 - более 6 лет
        }
        return parsedExperience;
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

    private Document connectDocumentToUrl(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
