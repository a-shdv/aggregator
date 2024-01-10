package com.company.habrparser.service;

import com.company.habrparser.rabbitmq.dto.SendMessageDto;
import com.company.habrparser.rabbitmq.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ParserService {
    private Document doc;
    private Elements sections;
    private Elements links;
    private final Elements requirements = new Elements();
    private final RabbitMqService rabbitMqService;

    @Autowired
    public ParserService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void parseWebPage() {
        final String url = "https://career.habr.com/vacancies/1000120027";
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        sections = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left section");
        links = sections.get(0).getElementsByClass("link-comp link-comp--appearance-dark");

        for (int i = 0; i < links.size() - 1; i++)
            requirements.add(links.get(i));

        SendMessageDto vacancy = SendMessageDto.builder()
                .title(sections.get(0).getElementsByClass("page-title__title").text())
                .date(sections.get(0).getElementsByClass("basic-date").text())
                .salary(sections.get(0).getElementsByClass("basic-salary basic-salary--appearance-vacancy-header").text())
                .company(links.last().text())
                .requirements(requirements.text())
                .description(sections.get(1).text().replace("Описание вакансии О компании и команде", "О компании и команде:\n").replace("Ожидания от кандидата", "\nОжидания от кандидата:\n").replace("Условия работы", "\nУсловия работы:").replace("Необходимые знания:", "\nНеобходимые знания:\n").replace("Список задач:", "\nСписок задач:\n").replace("Бонусы", "\nБонусы:\n").replace("Дополнительные инструкции", "\nДополнительные инструкции:\n").replace("Поделиться:", "").replace("Обязанности", "\nОбязанности\n").replace("Требования", "\nТребования\n").replace("Условия", "\nУсловия\n").replace("Зона ответственности:", "\nЗона ответственности:\n").replace("Ключевые компетенции для кандидата", "\nКлючевые компетенции для кандидата\n"))
                .source(url)
                .build();
        sendMessageToRabbit(vacancy);
    }

    private void sendMessageToRabbit(SendMessageDto sendMessageDto) {
        rabbitMqService.send(sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}
