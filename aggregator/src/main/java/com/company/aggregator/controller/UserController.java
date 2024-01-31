package com.company.aggregator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class UserController {

    @GetMapping("/sign-in")
    public String signIn() {

        return "users/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp() {

        return "users/sign-up";
    }
}
