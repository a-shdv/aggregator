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
    private final RabbitMqService rabbitMqService;

    @Autowired
    public ParserService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void parseAll() {
        String title = "java";
        final String url = "https://career.habr.com/vacancies?q=" + title + "&type=all";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (doc != null) {
            final Elements sections = doc.getElementsByClass("section-box");
            for (int i = 1; i < 26; i++) {
                System.out.println(sections.get(i).text());
                System.out.println(sections.get(i).getElementsByClass("vacancy-card__title-link").first().absUrl("href"));
            }

        }

    }

    public void parseWebPage() {
        final String url = "https://career.habr.com/vacancies/1000120027";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (doc != null) {
            final Elements sections = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left section");
            final Elements links = sections.get(0).getElementsByClass("link-comp link-comp--appearance-dark");
            final Elements requirements = new Elements();

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
    }

    private void sendMessageToRabbit(SendMessageDto sendMessageDto) {
        rabbitMqService.send(sendMessageDto);
        log.info("SENT: {}", sendMessageDto);
    }
}
