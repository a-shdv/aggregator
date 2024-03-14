package com.company.aggregator.controllers;

import com.company.aggregator.exceptions.VacancyNotFoundException;
import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import com.company.aggregator.rabbitmq.dtos.SendMessageDto;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.rabbitmq.services.RabbitMqService;
import com.company.aggregator.services.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;
    private final RabbitMqService rabbitMqService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitProperties;

    @GetMapping("/{id}")
    public String findVacancy(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Vacancy vacancy = null;
        try {
            vacancy = vacancyService.findById(id);
        } catch (VacancyNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
        model.addAttribute("vacancy", vacancy);
        return "vacancies/vacancy";
    }

    @GetMapping
    public String findVacancies(@AuthenticationPrincipal User user,
                                @RequestParam(required = false, defaultValue = "0") int page,
                                @RequestParam(required = false, defaultValue = "12") int size,
                                Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        CompletableFuture<Page<Vacancy>> vacancies = vacancyService.findVacanciesAsync(user, PageRequest.of(page, size));
        model.addAttribute("vacancies", vacancies.join());
        return "vacancies/vacancies";
    }


    @PostMapping
    public String findVacancies(@AuthenticationPrincipal User user, String title, BigDecimal salary, boolean onlyWithSalary,
                                int experience, int cityId, boolean isRemoteAvailable) {
        rabbitMqService.send(SendMessageDto.builder()
                .username(user.getUsername())
                .title(title)
                .salary(salary)
                .onlyWithSalary(onlyWithSalary)
                .experience(experience)
                .cityId(cityId)
                .isRemoteAvailable(isRemoteAvailable)
                .build());
        return "redirect:/vacancies";
    }


    @PostMapping("/clear")
    public String deleteVacancies(@AuthenticationPrincipal User user) {
        vacancyService.deleteVacanciesByUserAsync(user);
        return "redirect:/vacancies";
    }

    @PostMapping("/stop-consuming-messages")
    public String stopConsumingMessages(@RequestParam("isConsumingCancelled") Boolean isConsumingCancelled, RedirectAttributes redirectAttributes) {
        SendMessageDto dto = SendMessageDto.builder().isConsumingCancelled(isConsumingCancelled).build();
        rabbitTemplate.convertAndSend(rabbitProperties.getRoutingKeyToSend(), dto);
        log.info("SENT: {}", dto);
        return "redirect:/";
    }
}
