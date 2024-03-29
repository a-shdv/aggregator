package com.company.statisticsparser.rabbitmq.service;


import com.company.statisticsparser.rabbitmq.dto.ReceiveMessageDto;
import com.company.statisticsparser.rabbitmq.dto.SendMessageDto;

public interface RabbitMqService {

    void receive(ReceiveMessageDto message);


    void send(SendMessageDto message);

}
