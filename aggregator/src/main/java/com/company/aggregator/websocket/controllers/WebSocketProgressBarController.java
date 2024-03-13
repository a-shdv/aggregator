package com.company.aggregator.websocket.controllers;

import com.company.aggregator.websocket.models.WebSocketProgressBarMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketProgressBarController {
    @MessageMapping("/sendMessage")
    @SendTo("/topic/progressbar")
    public WebSocketProgressBarMessage sendMessage(@Payload WebSocketProgressBarMessage webSocketProgressBarMessage) {
        return webSocketProgressBarMessage;
    }

    @MessageMapping("/receiveMessage")
    public WebSocketProgressBarMessage receiveMessage(@Payload WebSocketProgressBarMessage webSocketProgressBarMessage,
                                                      SimpMessageHeaderAccessor headerAccessor) {
//        headerAccessor.getSessionAttributes().put("username", webSocketProgressBarMessage.getSender());
        return webSocketProgressBarMessage;
    }
}
