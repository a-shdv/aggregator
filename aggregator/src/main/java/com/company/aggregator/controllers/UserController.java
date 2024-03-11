package com.company.aggregator.controllers;

import com.company.aggregator.dtos.ChangePasswordDto;
import com.company.aggregator.dtos.ChangeUsernameDto;
import com.company.aggregator.dtos.SignUpDto;
import com.company.aggregator.exceptions.OldPasswordIsWrongException;
import com.company.aggregator.exceptions.PasswordsDoNotMatchException;
import com.company.aggregator.exceptions.UserAlreadyExistsException;
import com.company.aggregator.models.User;
import com.company.aggregator.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("/account-info")
    public String accountInfo(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "users/account-info";
    }

    @GetMapping("/change-username")
    public String changeUsername(Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "users/change-username";
    }

    @PostMapping("/change-username")
    public String changeUsername(@AuthenticationPrincipal User user, @ModelAttribute ChangeUsernameDto changeUsernameDto, RedirectAttributes redirectAttributes) {
        try {
            if (userService.findUserByUsernameAsync(changeUsernameDto.getUsername()).join() != null) {
                throw new UserAlreadyExistsException("User with username " + changeUsernameDto.getUsername() + " already exists!");
            }
            userService.changeUsername(user, changeUsernameDto);
            redirectAttributes.addFlashAttribute("success", "Username has been changed successfully!");
        } catch (UserAlreadyExistsException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/change-username";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "users/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal User user, @ModelAttribute ChangePasswordDto changePasswordDto, RedirectAttributes redirectAttributes) {
        try {
            if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
                throw new OldPasswordIsWrongException("Wrong old password!");
            }
            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
                throw new PasswordsDoNotMatchException("Passwords do not match!");
            }
            userService.changePassword(user, changePasswordDto);
            redirectAttributes.addFlashAttribute("success", "Password has been changed successfully!");
        } catch (OldPasswordIsWrongException | PasswordsDoNotMatchException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/change-password";
    }
}
