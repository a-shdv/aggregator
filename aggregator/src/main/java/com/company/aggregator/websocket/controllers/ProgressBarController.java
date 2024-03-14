package com.company.aggregator.websocket.controllers;

import com.company.aggregator.dtos.WebSocketMessage;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.websocket.models.ProgressBarMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ProgressBarController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitMqService rabbitMqService;

//    @MessageMapping("/fromJava")
//    @SendTo("/topic/progressbar")
//    public ProgressBarMessage sendMessage(@Payload ProgressBarMessage progressBarMessage) {
//        progressBarMessage.setContent("rer");
//        progressBarMessage.setType("rer");
//        progressBarMessage.setSender("rer");
//        messagingTemplate.convertAndSend("/topic/progressbar", progressBarMessage);
//
//        return progressBarMessage;
//    }

    @MessageMapping("/toJava")
    public ProgressBarMessage receiveMessage(@RequestBody WebSocketMessage msg,
                                             @Payload ProgressBarMessage progressBarMessage) {
        rabbitMqService.send(SendMessageDto.builder()
                .username(msg.getUsername())
                .title(msg.getTitle())
                .salary(msg.getSalary())
                .onlyWithSalary(msg.getOnlyWithSalary())
                .experience(msg.getExperience())
                .cityId(msg.getCityId())
                .isRemoteAvailable(msg.getIsRemoteAvailable())
                .build());
        return progressBarMessage;
    }
}
