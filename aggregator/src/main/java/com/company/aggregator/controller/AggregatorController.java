package com.company.aggregator.controller;

import com.company.aggregator.model.User;
import com.company.aggregator.model.Vacancy;
import com.company.aggregator.rabbitmq.dto.SendMessageDto;
import com.company.aggregator.rabbitmq.service.RabbitMqService;
import com.company.aggregator.service.AggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AggregatorController {
    private final AggregatorService aggregatorService;
    private final RabbitMqService rabbitMqService;

    @GetMapping
    public String findVacancies(@AuthenticationPrincipal User user,
                                @RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "10") int size,
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
        return "home";
    }


    @PostMapping
    public String findVacanciesByTitle(@AuthenticationPrincipal User user, String title, int amount, BigDecimal salary, boolean onlyWithSalary,
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
