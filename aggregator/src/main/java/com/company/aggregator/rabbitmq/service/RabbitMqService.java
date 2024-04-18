package com.company.aggregator.rabbitmq.service;


import com.company.aggregator.rabbitmq.dto.CancelParsingDto;

import java.util.List;

public interface RabbitMqService {
    void receiveStatisticsParser(com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto message);

    void receiveVacanciesParser(List<com.company.aggregator.rabbitmq.dto.vacancies.ReceiveMessageDto> message);

    void sendToVacanciesParser(com.company.aggregator.rabbitmq.dto.vacancies.SendMessageDto message);

    void sendToVacanciesParserCancel(CancelParsingDto cancelParsingDto);

    void sendToStatisticsParser(com.company.aggregator.rabbitmq.dto.statistics.SendMessageDto message);

}
