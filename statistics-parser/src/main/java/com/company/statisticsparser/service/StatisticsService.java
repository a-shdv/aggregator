package com.company.statisticsparser.service;

import com.company.statisticsparser.rabbitmq.dto.ReceiveMessageDto;
import com.company.statisticsparser.rabbitmq.dto.SendMessageDto;
import com.company.statisticsparser.rabbitmq.service.RabbitMqSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
    private final RabbitMqSenderService rabbitMqSenderService;

    private static String replaceNumbersAftersDots(String input) {
        // Replace digits after dots with empty string
        String pattern = "(\\.\\d{2})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String switchCurrency(String currency) {
        return switch (currency) {
            case "" -> "RUB, ₽";
            case "eur" -> "EUR, €";
            case "usd" -> "USD, $";
            default -> "";
        };
    }

    @Async
    public CompletableFuture<Void> findStatistics(ReceiveMessageDto receiveMessageDto) {
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
            url.append("&c=").append(receiveMessageDto.getCurrency());
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
        } else {
            rabbitMqSenderService.send(SendMessageDto.builder().build());
            return CompletableFuture.completedFuture(null);
        }


        AtomicReference<String> avgSalaryTitle = new AtomicReference<>();
        AtomicReference<String> avgSalaryDesc = new AtomicReference<>();
        AtomicReference<String> medianSalaryTitle = new AtomicReference<>();
        AtomicReference<String> medianSalaryDesc = new AtomicReference<>();
        AtomicReference<String> modalSalaryTitle = new AtomicReference<>();
        AtomicReference<String> modalSalaryDesc = new AtomicReference<>();
        if (elements != null) {
            elements.forEach(el -> {
                el.getElementsByClass("chart-section__info").forEach(info -> {
                    String title = info.getElementsByClass("chart-section__info-title").text();
                    String desc = info.getElementsByClass("chart-section__info-desc").text();
                    if (title.contains("Средняя заработная плата")) {
                        avgSalaryTitle.set(title);
                        avgSalaryDesc.set(desc);
                    } else if (title.contains("Медианная заработная плата")) {
                        medianSalaryTitle.set(title);
                        medianSalaryDesc.set(desc);
                    } else if (title.contains("Модальная заработная")) {
                        modalSalaryTitle.set(title);
                        modalSalaryDesc.set(desc);
                    }
                });
                rabbitMqSenderService.send(SendMessageDto.builder()
                        .username(receiveMessageDto.getUsername())
                        .avgSalaryTitle(replaceNumbersAftersDots(avgSalaryTitle.toString()))
                        .avgSalaryDescription(avgSalaryDesc.toString())
                        .medianSalaryTitle(replaceNumbersAftersDots(medianSalaryTitle.toString()))
                        .medianSalaryDescription(medianSalaryDesc.toString())
                        .modalSalaryTitle(replaceNumbersAftersDots(modalSalaryTitle.toString()))
                        .modalSalaryDescription(modalSalaryDesc.toString())
                        .profession(receiveMessageDto.getProfession())
                        .city(receiveMessageDto.getCity())
                        .year(receiveMessageDto.getYear())
                        .currency(switchCurrency(receiveMessageDto.getCurrency()))
                        .build());
            });

        } else {
            log.error("Could not parse elements!");
        }
        return CompletableFuture.completedFuture(null);
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
