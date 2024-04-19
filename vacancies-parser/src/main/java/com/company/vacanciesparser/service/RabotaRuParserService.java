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

@Service
@RequiredArgsConstructor
@Slf4j
// 30 вакансий на одной странице
public class RabotaRuParserService {
    @Value("${websites.rabota.num-of-elements-per-page}")
    private Integer amount;
    private final RabbitMqSenderService rabbitMqSenderService;

    @Async("asyncExecutor")
    public CompletableFuture<Void> findVacancies(ReceiveMessageDto receiveMessageDto) {
        System.out.println("rabota: " + Thread.currentThread().getName());

        String username = receiveMessageDto.getUsername();
        String query = receiveMessageDto.getTitle();
        BigDecimal salary = receiveMessageDto.getSalary();
        Integer experience = receiveMessageDto.getExperience();
        Integer cityId = receiveMessageDto.getCityId();
        Boolean isRemoteAvailable = receiveMessageDto.getIsRemoteAvailable();
        Integer numOfRequests = receiveMessageDto.getNumOfRequests();

        int prevPage;
        int currPage = -1 + numOfRequests;
        StringBuilder url = new StringBuilder(
                "https://www.rabota.ru/vacancy" +
                        "?query=" + query +
                        "&min_salary=" + salary +
                        "&experience_ids=" + parseExperience(experience) +
                        "&page=" + currPage
        );
        String parsedCityId = parseCityId(cityId);
        if (!parsedCityId.isEmpty()) {
            url.replace(0, 30, "https://" + parsedCityId + ".rabota.ru/vacancy/");
        }
        if (isRemoteAvailable) {
            url.append("&schedule_ids=6");
        }
        Document doc = connectDocumentToUrl(url.toString());
        Elements elements = null;
        if (doc != null) {
            elements = doc.getElementsByClass("r-serp__item r-serp__item_vacancy");
        }
        if (elements != null && !elements.isEmpty()) {
            List<SendMessageDto> sendMessageDtoList = new ArrayList<>();

            while (currPage <= amount / elements.size()) {
                elements.forEach(it -> {
                    String source = it.getElementsByAttribute("href").first().absUrl("href");
                    String title = it.getElementsByClass("vacancy-preview-card__title").first().text();
                    String date = parseUpdatedDate(source).toString();
                    String vacancySalary = it.getElementsByClass("vacancy-preview-card__salary").first().text();
                    String requirements = "Нет поддержки ключевых слов для rabota.ru.";
                    String company = it.getElementsByClass("vacancy-preview-card__company-name").first().text();
                    String description = it.getElementsByClass("vacancy-preview-card__short-description").first().text();
                    String schedule = it.getElementsByClass("vacancy-preview-location__address-text").first() != null ? it.getElementsByClass("vacancy-preview-location__address-text").first().text() : "";
                    String logo = it.getElementsByClass("r-image__image").first() != null ? it.getElementsByClass("r-image__image").first().absUrl("src") : null;

                    SendMessageDto dto = SendMessageDto.builder()
                            .username(username)
                            .title(title)
                            .date(date)
                            .salary(vacancySalary)
                            .company(company)
                            .requirements(requirements)
                            .description(description)
                            .schedule(schedule)
                            .source(source)
                            .logo(logo)
                            .build();

                    sendMessageDtoList.add(dto);
                });

                prevPage = currPage;
                currPage++;
                url.replace(
                        url.indexOf("&page=") + prevPage,
                        url.lastIndexOf("&page=") + prevPage,
                        "&page=" + currPage
                );

                if (sendMessageDtoList.size() == elements.size()) {
                    rabbitMqSenderService.send(sendMessageDtoList);
                    sendMessageDtoList.clear();
                }
            }

            // Отправка оставшихся сообщений, если в списке осталось < sendMessageDtoListMaxSize сообщений после парсинга
            if (!sendMessageDtoList.isEmpty()) {
                rabbitMqSenderService.send(sendMessageDtoList);
                sendMessageDtoList.clear();
            }


        } else {
            log.error("Could not parse elements: {}", this.getClass().getName());
        }
        return CompletableFuture.completedFuture(null);
    }

    private String parseExperience(Integer experience) {
        String result = "";
        switch (experience) {
            case 1 -> result = "0"; // нет опыта - нет опыта
            case 2 -> result = "2"; // 1-2 года - 1-3 года
            case 3 -> result = "3"; // 3-4 года - 3-6 лет
        }
        return result;
    }

    private String parseCityId(Integer cityId) {
        String parsedCityId = "";
        switch (cityId) {
            case 0 -> parsedCityId = "";  // Москва
            case 1 -> parsedCityId = "spb"; // СПБ
            case 2 -> parsedCityId = "eburg"; // ЕКБ
            case 3 -> parsedCityId = "nsk"; // Новосибирск
            case 4 -> parsedCityId = "kazan"; // Казань
            case 5 -> parsedCityId = "nn"; // Нижний Новгород
            case 6 -> parsedCityId = "ulv"; // Ульяновск
            case 7 -> parsedCityId = "tol"; // Тольятти
            case 8 -> parsedCityId = "astrakhan"; // Астрахань
            case 9 -> parsedCityId = "ufa"; // Уфа
            case 10 -> parsedCityId = "krasnoyarsk"; // Красноярск
            case 11 -> parsedCityId = "chelyabinsk"; // Челябинск
            case 12 -> parsedCityId = "samara"; // Самара
            case 13 -> parsedCityId = "rostov"; // Ростов-на-Дону
            case 14 -> parsedCityId = "krasnodar"; // Краснодар
            case 15 -> parsedCityId = "omsk"; // Омск
            case 16 -> parsedCityId = "voronezh"; // Воронеж
            case 17 -> parsedCityId = "perm"; // Пермь
            case 18 -> parsedCityId = "volgograd"; // Волгоград
            case 20 -> parsedCityId = "saratov"; // Саратов
            case 21 -> parsedCityId = "tumen"; // Тюмень
            case 23 -> parsedCityId = "barnaul"; // Барнаул
            case 24 -> parsedCityId = "mahachkala"; // Махачкала
            case 25 -> parsedCityId = "izhevsk"; // Ижевск
            case 26 -> parsedCityId = "khabarovsk"; // Хабаровск
            case 27 -> parsedCityId = "irkutsk"; // Иркутск
            case 28 -> parsedCityId = "vladivostok"; // Владивосток
            case 29 -> parsedCityId = "yaroslavl"; // Ярославль
            case 30 -> parsedCityId = "sevastopol"; // Севастополь
        }
        return parsedCityId;
    }

    private String parseUpdatedDate(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        if (doc != null) {
            return doc.getElementsByClass("vacancy-system-info__updated-date").text();
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
