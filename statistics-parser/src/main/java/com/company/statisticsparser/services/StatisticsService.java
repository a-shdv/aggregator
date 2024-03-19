package com.company.statisticsparser.services;

import com.company.statisticsparser.rabbitmq.dtos.ReceiveMessageDto;
import com.company.statisticsparser.rabbitmq.services.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
//    private final RabbitMqService rabbitMqService;

    @Async
    public CompletableFuture<Void> test(ReceiveMessageDto receiveMessageDto) {
//        rabbitMqService.send(receiveMessageDto);
        StringBuilder url = new StringBuilder("https://gorodrabot.ru/salary" +
                "?p=" + receiveMessageDto.getProfession() + "&l=" + receiveMessageDto.getCity() + "&c=" + receiveMessageDto.getCurrency());

        Document doc = connectDocumentToUrl(url.toString());
        Elements elements = null;
        if (doc != null) {
            elements = doc
                    .getElementsByClass("");
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
