package com.company.aggregator.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// STOMP - стриминговый текст-ориентированный протокол передачи сообщений
// Вебсокеты не обеспечивают некоторые нужные условия, например, мы не может ограничить передачу сообщений
// конкретному пользователю или группе пользователей
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //withSockJS is used to enable WS if browser of client doesn't support
        registry.addEndpoint("/ws").withSockJS();
    }

    //it uses to route messages from one client to another
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // messages whose destination ("/app")
        // messages should be routed by message-handling (@MessageHandling) methods
        registry.setApplicationDestinationPrefixes("/app");

        //should be routed broker
        //message broker broadcast messages to all connected clients
        //you can use RabbitMQ or ActiveMQ to broadcast
        registry.enableSimpleBroker("/topic");
    }
}
