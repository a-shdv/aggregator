package com.company.aggregator.rabbitmq.service;


import java.util.List;

public interface RabbitMqService {
    void receiveStatisticsParser(com.company.aggregator.rabbitmq.dto.statistics.ReceiveMessageDto message);

    void receiveVacanciesParser(List<com.company.aggregator.rabbitmq.dto.vacancy.ReceiveMessageDto> message);

    void sendToVacanciesParser(com.company.aggregator.rabbitmq.dto.vacancy.SendMessageDto message);

    void sendToStatisticsParser(com.company.aggregator.rabbitmq.dto.statistics.SendMessageDto message);

}
