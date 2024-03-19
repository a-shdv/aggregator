package com.company.aggregator.dtos;

import com.company.aggregator.rabbitmq.dtos.statistics.SendMessageDto;
import lombok.Getter;

public record StatisticsDto(@Getter String profession, @Getter String city, @Getter String year, @Getter String currency) {
    public StatisticsDto(String profession, String city, String year, String currency) {
        this.profession = profession;
        this.city = city;
        this.year = year;
        this.currency = currency;
    }

    public static SendMessageDto toSendMessageDto (StatisticsDto statisticsDto) {
        return SendMessageDto.builder()
                .profession(statisticsDto.getProfession())
                .city(statisticsDto.getCity())
                .year(statisticsDto.getYear())
                .currency(statisticsDto.getCurrency())
                .build();
    }
}
