package com.company.habrparser.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebPage {
    String title; // название вакансии
    String date; // дата публикации
    String salary; // заработная плата
    String company; // название компании
    String requirements; // требования
    String locationAndEmploymentType;

    @Override
    public String toString() {
        return "WebPage {" +
                "\n title='" + title + '\'' +
                ",\n date='" + date + '\'' +
                ",\n salary='" + salary + '\'' +
                ",\n company='" + company + '\'' +
                ",\n requirements='" + requirements + '\'' +
                ",\n locationAndEmploymentType='" + locationAndEmploymentType + '\'' + "\n" +
                '}';
    }
}
