package com.company.aggregator.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    @Value("${constants.heartbeat-url}")
    private String heartbeatUrl;
    private final RestTemplate restTemplate;
    private boolean isParserAvailable;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("isParserAvailable", isParserAvailable);
        return "home";
    }

    @Scheduled(initialDelay = 2_000, fixedDelay = 10_000) // TODO поменять fixedDelay
    public void sendHeartBeat() {
        try {
            restTemplate.getForEntity(heartbeatUrl, String.class).getStatusCode().is2xxSuccessful();
            isParserAvailable = true;
        } catch (ResourceAccessException ex) {
            isParserAvailable = false;
        }
    }
}
