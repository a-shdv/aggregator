package com.company.aggregator.rabbitmq.services;


import java.util.List;

public interface RabbitMqService {
    void receiveStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.ReceiveMessageDto message);

    void receiveVacanciesParser(List<com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto> message);

    void sendToVacanciesParser(com.company.aggregator.rabbitmq.dtos.vacancies.SendMessageDto message);

    void sendToStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.SendMessageDto message);

}
