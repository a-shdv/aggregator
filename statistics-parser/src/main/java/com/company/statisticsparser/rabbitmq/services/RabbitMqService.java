package com.company.statisticsparser.rabbitmq.services;


import com.company.statisticsparser.rabbitmq.dtos.ReceiveMessageDto;
import com.company.statisticsparser.rabbitmq.dtos.SendMessageDto;

public interface RabbitMqService {

    void receive(ReceiveMessageDto message);


    void send(SendMessageDto message);

}
