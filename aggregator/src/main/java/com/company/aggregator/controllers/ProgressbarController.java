package com.company.aggregator.controllers;

import com.company.aggregator.dtos.VacancyDto;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.websockets.dtos.ProgressBarMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ProgressbarController {
    private final RabbitMqService rabbitMqService;


    //registry.setApplicationDestinationPrefixes("/app")
    //messages from clients
    //routed... | full address - /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public VacancyDto receiveMessageFromWs(@RequestBody VacancyDto vacancyDto) {
        rabbitMqService.send(SendMessageDto.builder()
                .username(vacancyDto.getUsername())
                .title(vacancyDto.getTitle())
                .salary(vacancyDto.getSalary())
                .onlyWithSalary(vacancyDto.getOnlyWithSalary())
                .experience(vacancyDto.getExperience())
                .cityId(vacancyDto.getCityId())
                .isRemoteAvailable(vacancyDto.getIsRemoteAvailable())
                .build());
        return vacancyDto;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ProgressBarMessageDto addUser(@Payload ProgressBarMessageDto progressBarMessageDto,
                                         SimpMessageHeaderAccessor headerAccessor) {
        //add username in web socket session
        headerAccessor.getSessionAttributes().put("username", progressBarMessageDto.getSender());
        return progressBarMessageDto;
    }
}
