package com.company.aggregator.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final RestTemplate restTemplate;
    @Value("${constants.vacancies-heartbeat-url}")
    private String vacanciesHeartbeatUrl;
    private boolean isVacanciesParserAvailable;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("isParserAvailable", isVacanciesParserAvailable);
        return "home";
    }

    @Scheduled(initialDelay = 2_000, fixedDelay = 10_000)
    public void sendHeartBeat() {
        try {
            restTemplate.getForEntity(vacanciesHeartbeatUrl, String.class).getStatusCode().is2xxSuccessful();
            isVacanciesParserAvailable = true;
        } catch (ResourceAccessException ex) {
            isVacanciesParserAvailable = false;
        }
    }
}
