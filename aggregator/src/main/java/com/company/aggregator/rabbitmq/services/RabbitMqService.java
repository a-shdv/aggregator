package com.company.aggregator.rabbitmq.services;


import com.company.aggregator.rabbitmq.dtos.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;

import java.util.List;

public interface RabbitMqService {

    void receive(List<ReceiveMessageDto> message);

    void send(SendMessageDto message);

}
