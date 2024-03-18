package com.company.aggregator.controllers;

import com.company.aggregator.exceptions.VacancyNotFoundException;
import com.company.aggregator.models.User;
import com.company.aggregator.models.Vacancy;
import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.company.aggregator.services.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;
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
        // TODO
        Page<Vacancy> vacancies = vacancyService.findVacanciesAsync(user, PageRequest.of(page, size)).join();
        model.addAttribute("vacancies", vacancies);
        return "vacancies/vacancies";
    }

    @PostMapping("/clear")
    public ResponseEntity<String> deleteVacancies(@AuthenticationPrincipal User user) {
        // todo java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.CompletableFuture$AsyncSupply@11d540a rejected from org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor$1@441fda4d[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 9]
        //	at java.base/java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:2065) ~[na:na]
        //	at java.base/java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:833) ~[na:na]
        //	at java.base/java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1365) ~[na:na]
        vacancyService.deleteVacanciesByUserAsync(user);
        return ResponseEntity.ok().body("Vacancies cleared successfully");
    }


}
