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

@Service
@RequiredArgsConstructor
@Slf4j
public class RabotaRuParserService {
    private final RabbitMqSenderService rabbitMqSenderService;
    private static final Integer amount = 10;

    @Async("jobExecutor")
    public void findVacancies(String username, String query, BigDecimal salary, Boolean onlyWithSalary,
                              Integer experience, Integer cityId, Boolean isRemoteAvailable) {
        int prevPage;
        int currPage = 1;
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
                    String schedule = it.getElementsByClass("vacancy-preview-location__address-text").first().text();
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
            log.error("Could not parse elements");
        }
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
