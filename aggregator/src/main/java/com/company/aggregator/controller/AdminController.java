package com.company.aggregator.controller;

import com.company.aggregator.dto.UserLockStatusDto;
import com.company.aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final UserService userService;

    @GetMapping
    public String adminPanel(@RequestParam(required = false, defaultValue = "0") int page,
                             @RequestParam(required = false, defaultValue = "10") int size,
                             Model model) {
        model.addAttribute("users", userService.findUsers(PageRequest.of(page, size)));
        return "admins/panel";
    }

    @PostMapping("/block")
    public String block(@ModelAttribute UserLockStatusDto userLockStatusDto) {
        userService.block(userLockStatusDto);
        return "redirect:/admin";
    }

    @PostMapping("/unblock")
    public String unblock(@ModelAttribute UserLockStatusDto userLockStatusDto) {
        userService.unblock(userLockStatusDto);
        return "redirect:/admin";
    }
}
