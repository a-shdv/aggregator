package com.company.aggregator.websocket.controllers;

import com.company.aggregator.dtos.WebSocketMessage;
import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.websocket.models.WebSocketProgressBarMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class WebSocketProgressBarController {
    private SimpMessagingTemplate messagingTemplate;
    private final RabbitMqService rabbitMqService;

//    @MessageMapping("/sendMessage")
//    @SendTo("/topic/progressbar")
//    public WebSocketProgressBarMessage sendMessage(@Payload WebSocketProgressBarMessage webSocketProgressBarMessage) {
//
//        messagingTemplate.convertAndSend("/topic/progressbar", webSocketProgressBarMessage);
//
//        return webSocketProgressBarMessage;
//    }

    @MessageMapping("/receiveMessage")
    public WebSocketProgressBarMessage receiveMessage(@AuthenticationPrincipal User user, @RequestBody WebSocketMessage object,/* Boolean isRemoteAvailable,*/
                                                      @Payload WebSocketProgressBarMessage webSocketProgressBarMessage,
                                                      SimpMessageHeaderAccessor headerAccessor) {
        System.out.println(object);
        //        rabbitMqService.send(SendMessageDto.builder()
//                .username(user.getUsername())
//                .title(title)
//                .salary(new BigDecimal(salary))
////                .onlyWithSalary(onlyWithSalary)
//                .experience(experience)
//                .cityId(cityId)
////                .isRemoteAvailable(isRemoteAvailable)
//                .build());
        return webSocketProgressBarMessage;
    }
}
