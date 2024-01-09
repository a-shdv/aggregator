package com.company.habrparser.service;

import com.company.habrparser.model.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ParserService {
    @EventListener(ApplicationReadyEvent.class)
    public void parseWebPage() {
        WebPage webPage;
        Document doc = null;
        try {
            doc = Jsoup.connect("https://career.habr.com/vacancies/1000135009").get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Elements header = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left div");
        Elements body = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left div div.vacancy-description__text");

        WebPage page = WebPage.builder()
                .title(header.first().getElementsByClass("page-title__title").text()) // название вакансии
                .date(header.first().getElementsByClass("basic-date").text()) // дата публикации
                .salary(header.first().getElementsByClass("basic-salary basic-salary--appearance-vacancy-header").text()) // заработная плата
                .companyTitle(header.first().getElementsByClass("link-comp link-comp--appearance-dark").last().text()) // название компании
                .build();
        page.setCompanyDescription(body.first().getElementsByClass("style-ugc").get(0).text()); // о компании (описание компании)
        page.setExpectations(body.first().getElementsByClass("style-ugc").get(1).text()); // ожидания от кандидата

        page.setResponsibilities(body.first().getElementsByClass("style-ugc").get(2).select("ul").get(0).text()); // обязанности
        page.setRequirements(body.first().getElementsByClass("style-ugc").get(2).select("ul").get(1).text()); // требования
        page.setConditions(body.first().getElementsByClass("style-ugc").get(2).select("ul").get(2).text()); // условия
        page.setBonuses(body.first().getElementsByClass("style-ugc").get(3).text()); // бонусы
        page.setAdditionals(body.first().getElementsByClass("style-ugc").get(4).text()); // дополнительные инструкции

        System.out.println("Название вакансии: " + page.getTitle());
        System.out.println("Дата публикации: " + page.getDate());
        System.out.println("Заработная плата: " + page.getSalary());
        System.out.println("Название компании: " + page.getCompanyTitle());
        System.out.println("О компании: " + page.getCompanyDescription());
        System.out.println("Ожидания от кандидата: " + page.getExpectations());
        System.out.println("Обязанности: " + page.getResponsibilities());
        System.out.println("Требования: " + page.getRequirements());
        System.out.println("Условия: " + page.getConditions());
        System.out.println("Бонусы: " + page.getBonuses());
        System.out.println("Дополнительные инструкции: " + page.getAdditionals());
    }
}
