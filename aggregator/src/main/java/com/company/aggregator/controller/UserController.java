package com.company.aggregator.controller;

import com.company.aggregator.dto.FavouriteDto;
import com.company.aggregator.dto.SignUpDto;
import com.company.aggregator.exception.UserAlreadyExistsException;
import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.service.FavouriteService;
import com.company.aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public CompletableFuture<String> signUp(@ModelAttribute("signUpDto") SignUpDto dto, RedirectAttributes redirectAttributes) {
        return userService.findUserByUsernameAsync(dto.getUsername()).thenApply(foundUser -> {
            try {
                if (foundUser != null) {
                    throw new UserAlreadyExistsException("Пользователь уже существует: " + dto.getUsername());
                }
                userService.saveUserAsync(SignUpDto.toUser(dto));
                redirectAttributes.addFlashAttribute("success", "Пользователь успешно создан: " + dto.getUsername());
                return "redirect:/sign-in";
            } catch (UserAlreadyExistsException e) {
                log.info(e.getMessage());
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/sign-up";
            }
        });
    }
}
