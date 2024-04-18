package com.company.aggregator.ws.listener;

import com.company.aggregator.ws.dto.WebSocketReceiveMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (headerAccessor.getSessionAttributes() != null) {
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            if (username != null) {
                log.info("User Disconnected : " + username);

                WebSocketReceiveMessageDto webSocketReceiveMessageDto = new WebSocketReceiveMessageDto();
                webSocketReceiveMessageDto.setType(WebSocketReceiveMessageDto.MessageType.LEAVE);
                webSocketReceiveMessageDto.setSender(username);

                messageSendingOperations.convertAndSend("/topic/public", webSocketReceiveMessageDto);
            }
        }
    }
}
