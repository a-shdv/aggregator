package com.company.aggregator.controller;

import com.company.aggregator.rabbitmq.dto.SendMessageDto;
import com.company.aggregator.rabbitmq.service.RabbitMqService;
import com.company.aggregator.service.AggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AggregatorController {
    private final AggregatorService aggregatorService;
    private final RabbitMqService rabbitMqService;

    @PostMapping
    public String findVacanciesByTitle(String title, Model model) {
        rabbitMqService.send(SendMessageDto.builder()
                .title(title)
                .build());
        return "redirect:/";
    }

    @GetMapping
    public String findAll(@RequestParam(required = false, defaultValue = "0") int page,
                          @RequestParam(required = false, defaultValue = "10") int size,
                          Model model) {
        try {
            model.addAttribute("vacancies", aggregatorService.findAll(PageRequest.of(page, size)).get());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return "home";
    }
}
