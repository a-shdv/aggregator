package com.company.aggregator.service;

import com.company.aggregator.rabbitmq.dto.ReceiveMessageDto;
import org.springframework.stereotype.Service;

@Service
public class AggregatorService {
    public void findAll(ReceiveMessageDto receiveMessageDto) {
        System.out.println(receiveMessageDto);
        System.out.println(receiveMessageDto);
        System.out.println(receiveMessageDto);
        System.out.println(receiveMessageDto);
    }
}
