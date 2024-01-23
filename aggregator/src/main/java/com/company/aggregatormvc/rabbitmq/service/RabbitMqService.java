package com.company.aggregatormvc.rabbitmq.service;


import com.company.aggregatormvc.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregatormvc.rabbitmq.dto.SendMessageDto;

public interface RabbitMqService {

    void receive(ReceiveMessageDto message);

    void send(SendMessageDto message);

}
