package com.company.aggregator.dtos;

import lombok.Getter;

public record StatisticsDto(@Getter String profession, @Getter String city, @Getter String year, @Getter Integer currency) {
    public StatisticsDto(String profession, String city, String year, Integer currency) {
        this.profession = profession;
        this.city = city;
        this.year = year;
        this.currency = currency;
    }
}
