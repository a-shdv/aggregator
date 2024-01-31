package com.company.aggregator.rabbitmq.service;


import com.company.aggregator.model.User;
import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import com.company.aggregator.rabbitmq.dto.SendMessageDto;

import java.util.List;

public interface RabbitMqService {

    void receive(List<ReceiveMessageDto> message);

    void send(SendMessageDto message);

}
