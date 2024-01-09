package com.company.habrparser.service;

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
        Document doc = null;
        try {
            doc = Jsoup.connect("https://career.habr.com/vacancies/1000135009").get();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Elements headlines = doc.select("html body.vacancies_show_page div.page-container div.page-container__main div.page-width.page-width--responsive div.content-wrapper div.content-wrapper__main.content-wrapper__main--left div");
        System.out.printf("title: %s\n", headlines.first().getElementsByClass("page-title__title").text());
        System.out.printf("date: %s\n", headlines.first().getElementsByClass("basic-date").text());
        System.out.printf("salary: %s\n", headlines.first().getElementsByClass("basic-salary basic-salary--appearance-vacancy-header").text());
//        System.out.printf("requirements: %s\n", headlines.first().getElementsByClass("link-comp link-comp--appearance-dark").text());

        System.out.printf("companyName: %s\n", headlines.first().getElementsByClass("link-comp link-comp--appearance-dark").last().text());
    }
}
