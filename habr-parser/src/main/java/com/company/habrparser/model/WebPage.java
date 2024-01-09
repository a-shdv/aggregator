package com.company.habrparser.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebPage {
    String title; // название вакансии
    String date; // дата публикации
    String salary; // заработная плата
    String companyTitle; // название компании
    String companyDescription; // о компании (описание компании)
    String expectations; // ожидания от кандидата
    String responsibilities; // обязанности
    String requirements; // требования
    String conditions; // условия
    String bonuses; // бонусы
    String additionals; // дополнительные инструкции
}
