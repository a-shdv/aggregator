package com.company.parser.rabbitmq.service;

import com.company.parser.rabbitmq.dto.ReceiveMessageDto;
import com.company.parser.rabbitmq.dto.SendMessageDto;

public interface RabbitMqService {

    void receive(ReceiveMessageDto message);

    void send(SendMessageDto message);

}
