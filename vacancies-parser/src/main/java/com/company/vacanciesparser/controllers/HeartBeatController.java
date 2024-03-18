package com.company.vacanciesparser.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vacancies")
public class HeartBeatController {
    @GetMapping("/heartbeat")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok().body("ok");
    }
}
