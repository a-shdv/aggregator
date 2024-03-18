package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.rabbitmq.dtos.vacancies.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dtos.vacancies.SendMessageDto;

import java.util.List;

public interface RabbitMqService {

    void receive(List<ReceiveMessageDto> message);

    void sendToVacanciesParser(com.company.aggregator.rabbitmq.dtos.vacancies.SendMessageDto message);

    void sendToStatisticsParser(com.company.aggregator.rabbitmq.dtos.statistics.SendMessageDto message);

}
