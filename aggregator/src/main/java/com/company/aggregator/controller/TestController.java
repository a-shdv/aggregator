package com.company.aggregator.controller;

import com.company.aggregator.entity.Vacancy;
import com.company.aggregator.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final VacancyService vacancyService;

    @GetMapping
    public ResponseEntity<List<Vacancy>> findAll() {
        return ResponseEntity.ok().body(vacancyService.findAll());
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody(required = false) Vacancy vacancy) {
        if (vacancy == null) {
            vacancy = Vacancy.builder().build();
        }
        vacancyService.saveTest(vacancy);
        return ResponseEntity.ok().body("ok");
    }
}
