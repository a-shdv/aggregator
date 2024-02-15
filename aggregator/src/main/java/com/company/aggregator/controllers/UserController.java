package com.company.aggregator.controllers;

import com.company.aggregator.dtos.SignUpDto;
import com.company.aggregator.exceptions.UserAlreadyExistsException;
import com.company.aggregator.models.User;
import com.company.aggregator.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/sign-in")
    public String signIn(Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "users/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "users/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute("signUpDto") SignUpDto dto, RedirectAttributes redirectAttributes) {
        try {
            CompletableFuture<User> user = userService.findUserByUsernameAsync(dto.getUsername());
            if (user.join() != null) {
                throw new UserAlreadyExistsException("Пользователь уже существует: " + dto.getUsername());
            }
            userService.saveUserAsync(SignUpDto.toUser(dto));
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно создан: " + dto.getUsername());
            return "redirect:/sign-in";
        } catch (UserAlreadyExistsException ex) {
            log.info(ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/sign-up";
        }
    }
}
