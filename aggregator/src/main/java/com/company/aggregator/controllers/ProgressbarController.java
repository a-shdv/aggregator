package com.company.aggregator.controllers;

import com.company.aggregator.dtos.VacancyDto;
import com.company.aggregator.websockets.dtos.ProgressBarMessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ProgressbarController {
    //registry.setApplicationDestinationPrefixes("/app")
    //messages from clients
    //routed... | full address - /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public VacancyDto sendMessage(@RequestBody VacancyDto vacancyDto) {
        //send message to all connected clients
        System.out.println(vacancyDto);
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
