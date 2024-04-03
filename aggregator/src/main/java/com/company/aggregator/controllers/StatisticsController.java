package com.company.aggregator.controllers;

import com.company.aggregator.dtos.StatisticsDto;
import com.company.aggregator.models.Statistics;
import com.company.aggregator.models.User;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {
    private final RestTemplate restTemplate;
    private final RabbitMqService rabbitMqService;
    private final StatisticsService statisticsService;
    @Value("${constants.statistics-heartbeat-url}")
    private String statisticsHeartbeatUrl;
    private boolean isStatisticsParserAvailable;

    @GetMapping
    public String findStatistics(@AuthenticationPrincipal User user, Model model) {
        Statistics statistics = statisticsService.findStatisticsByUsername(user.getUsername());
        model.addAttribute("isParserAvailable", isStatisticsParserAvailable);
        model.addAttribute("statistics", statistics);
        return "statistics/statistics";
    }

    @PostMapping
    public String findStatistics(@ModelAttribute("statisticsDto") StatisticsDto statisticsDto) {
        statisticsService.deleteStatistics(statisticsDto);
        rabbitMqService.sendToStatisticsParser(StatisticsDto.toSendMessageDto(statisticsDto));
        return "redirect:/";
    }

    @Scheduled(initialDelay = 2_000, fixedDelay = 10_000)
    public void sendHeartBeat() {
        try {
            restTemplate.getForEntity(statisticsHeartbeatUrl, String.class).getStatusCode().is2xxSuccessful();
            isStatisticsParserAvailable = true;
        } catch (ResourceAccessException ex) {
            isStatisticsParserAvailable = false;
        }
    }
}
