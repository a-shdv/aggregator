package com.company.aggregator.controllers;

import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.services.AggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AggregatorController {
    private final AggregatorService aggregatorService;
    private final RabbitMqService rabbitMqService;
    private final RestTemplate restTemplate;

    @Value("${constants.heartbeat-url}")
    private String heartbeatUrl;

    private boolean isParserAvailable;

    @Scheduled(initialDelay = 2_000, fixedDelay = 1_000) // TODO поменять fixedDelay
    public void sendHeartBeat() {
        try {
            restTemplate.getForEntity(heartbeatUrl, String.class).getStatusCode().is2xxSuccessful();
            isParserAvailable = true;
        } catch (ResourceAccessException ex) {
            isParserAvailable = false;
        }
    }

    @GetMapping
    public String findVacancies(@AuthenticationPrincipal User user,
                                @RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "9") int size,
                                Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        CompletableFuture<Page<Vacancy>> vacancies = aggregatorService.findVacanciesAsync(user, PageRequest.of(page, size));
        model.addAttribute("vacancies", vacancies.join());
        model.addAttribute("isParserAvailable", isParserAvailable);
        return "home";
    }


    @PostMapping
    public String findVacancies(@AuthenticationPrincipal User user, String title, int amount, BigDecimal salary, boolean onlyWithSalary,
                                int experience, int cityId, boolean isRemoteAvailable) {
        rabbitMqService.send(SendMessageDto.builder()
                .username(user.getUsername())
                .title(title)
                .amount(amount)
                .salary(salary)
                .onlyWithSalary(onlyWithSalary)
                .experience(experience)
                .cityId(cityId)
                .isRemoteAvailable(isRemoteAvailable)
                .build());
        return "redirect:/";
    }


    @PostMapping("/clear")
    public String deleteVacancies(@AuthenticationPrincipal User user) {
        aggregatorService.deleteVacanciesByUserAsync(user);
        return "redirect:/";
    }
}
