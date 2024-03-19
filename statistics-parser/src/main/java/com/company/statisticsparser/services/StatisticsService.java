package com.company.statisticsparser.services;

import com.company.statisticsparser.rabbitmq.dtos.ReceiveMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
//    private final RabbitMqService rabbitMqService;

    public void findStatistics(ReceiveMessageDto receiveMessageDto) {
//        rabbitMqService.send(receiveMessageDto);
//        StringBuilder url = new StringBuilder("https://gorodrabot.ru/salary"
//        + "?p=" + receiveMessageDto.getProfession()
//        + "&l=" + receiveMessageDto.getCity()
//        + "&c=" + receiveMessageDto.getCurrency());

        StringBuilder url = new StringBuilder("https://gorodrabot.ru/salary");

        // Профессия
        if (receiveMessageDto.getProfession() != null) {
            url.append("?p=").append(receiveMessageDto.getProfession());
        }

        // Город
        if (receiveMessageDto.getCity() != null) {
            url.append("&l=").append(receiveMessageDto.getCity());
        }

        // Валюта
        if (receiveMessageDto.getCurrency() != null) {
            url.append("&с=").append(receiveMessageDto.getCurrency());
        }

        // Год
        if (receiveMessageDto.getYear() != null) {
            url.append("&y=").append(receiveMessageDto.getYear());
        }

        Document doc = connectDocumentToUrl(url.toString());
        Elements elements = null;
        if (doc != null) {
            elements = doc
                    .getElementsByClass("chart-options chart-list");
        }


        AtomicReference<String> avgSalaryTitle = new AtomicReference<>();
        AtomicReference<String> avgSalaryDesc = new AtomicReference<>();
        AtomicReference<String> medianSalaryTitle = new AtomicReference<>();
        AtomicReference<String> medianSalaryDesc = new AtomicReference<>();
        AtomicReference<String> modalSalaryTitle = new AtomicReference<>();
        AtomicReference<String> modalSalaryDesc = new AtomicReference<>();
        AtomicReference<String> picture0 = new AtomicReference<>();
        AtomicReference<String> picture1 = new AtomicReference<>();
        if (elements != null) {
            elements.forEach(el -> {
                el.getElementsByClass("chart-section__info").forEach(info -> {
                    String title = info.getElementsByClass("chart-section__info-title").text();
                    String desc = info.getElementsByClass("chart-section__info-desc").text();
                    if (title.contains("Средняя заработная плата в России")) {
                        avgSalaryTitle.set(title);
                        avgSalaryDesc.set(desc);
                    } else if (title.contains("Медианная заработная плата в России")) {
                        medianSalaryTitle.set(title);
                        medianSalaryDesc.set(title);
                    } else if (title.contains("Модальная заработная плата в России")) {
                        modalSalaryTitle.set(title);
                        modalSalaryDesc.set(desc);
                    }
                });

//                el.getElementsByClass("chart-section__download-img").forEach(picture -> {
//                    picture.getElementsByClass("link link_active").first();
//                    picture0.set(doc.getElementsByClass("chart-section__download-img").text());
//                });

            });
        } else {
            log.error("Could not parse elements!");
        }

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
