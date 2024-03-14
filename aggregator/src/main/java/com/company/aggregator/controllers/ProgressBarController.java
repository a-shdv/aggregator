package com.company.aggregator.controllers;

import com.company.aggregator.dtos.WebSocketMessageDto;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ProgressBarController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitMqService rabbitMqService;

    @MessageMapping("/toJava")
    public void receiveMessage(@RequestBody WebSocketMessageDto msg) {
        rabbitMqService.send(SendMessageDto.builder()
                .username(msg.getUsername())
                .title(msg.getTitle())
                .salary(msg.getSalary())
                .onlyWithSalary(msg.getOnlyWithSalary())
                .experience(msg.getExperience())
                .cityId(msg.getCityId())
                .isRemoteAvailable(msg.getIsRemoteAvailable())
                .build());
    }
}
