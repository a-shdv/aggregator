package com.company.aggregator.controller;

import com.company.aggregator.exception.vacancy.VacancyNotFoundException;
import com.company.aggregator.entity.User;
import com.company.aggregator.entity.Vacancy;
import com.company.aggregator.service.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
@Slf4j
public class VacancyController {
    private final VacancyService vacancyService;
    private final RestTemplate restTemplate;
    @Value("${constants.vacancies-heartbeat-url}")
    private String vacanciesHeartbeatUrl;
    private boolean isVacanciesParserAvailable;

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

        Page<Vacancy> vacancies = vacancyService.findVacancies(user, PageRequest.of(page, size));
        model.addAttribute("vacancies", vacancies);
        model.addAttribute("isParserAvailable", isVacanciesParserAvailable);
        return "vacancies/vacancies";
    }

    @PostMapping("/clear")
    public ResponseEntity<String> deleteVacancies(@AuthenticationPrincipal User user) {
        vacancyService.deleteVacanciesByUser(user);
        return ResponseEntity.ok().body("Vacancies cleared successfully");
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
