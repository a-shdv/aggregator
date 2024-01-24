package com.company.aggregator.rabbitmq.service;


import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dto.SendMessageDto;

public interface RabbitMqService {

    void receive(ReceiveMessageDto message);

    void send(SendMessageDto message);

}
