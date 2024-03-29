package com.company.aggregator.dto;

import com.company.aggregator.rabbitmq.dto.statistics.SendMessageDto;
import lombok.Getter;

public record StatisticsDto(@Getter String username, @Getter String profession, @Getter String city,
                            @Getter String year, @Getter String currency) {
    public StatisticsDto(String username,
                         String profession,
                         String city,
                         String year,
                         String currency) {
        this.username = username;
        this.profession = profession;
        this.city = city;
        this.year = year;
        this.currency = currency;
    }

    public static SendMessageDto toSendMessageDto(StatisticsDto statisticsDto) {
        return SendMessageDto.builder()
                .username(statisticsDto.getUsername())
                .profession(statisticsDto.getProfession())
                .city(statisticsDto.getCity())
                .year(statisticsDto.getYear())
                .currency(statisticsDto.getCurrency())
                .build();
    }
}
