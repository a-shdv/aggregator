package com.company.aggregator.controller;

import com.company.aggregator.dto.FavouriteDto;
import com.company.aggregator.exception.FavouriteAlreadyExistsException;
import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.service.EmailSenderService;
import com.company.aggregator.service.FavouriteService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favourites")
@Slf4j
public class FavouriteController {
    private final FavouriteService favouriteService;
    private final EmailSenderService emailSenderService;

    @GetMapping
    public String findFavourites(@AuthenticationPrincipal User user,
                                 @RequestParam(required = false, defaultValue = "0") int page,
                                 @RequestParam(required = false, defaultValue = "5") int size,
                                 Model model) {
        CompletableFuture<Page<Favourite>> favourites = favouriteService.findFavouritesAsync(user, PageRequest.of(page, size));
        model.addAttribute("favourites", favourites.join());
        return "users/favourites";
    }

    @PostMapping
    public String addToFavourites(@AuthenticationPrincipal User user,
                                  @ModelAttribute("favouriteDto") FavouriteDto favouriteDto,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (favouriteService.findBySourceAsync(favouriteDto.getSource()).join() != null) {
                throw new FavouriteAlreadyExistsException("Favourite already exists: " + favouriteDto.getSource());
            }
            favouriteService.addToFavouritesAsync(user, FavouriteDto.toFavourite(favouriteDto));
        } catch (FavouriteAlreadyExistsException e) {
            log.info(e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
        redirectAttributes.addFlashAttribute("success", "Favourite added successfully: " + favouriteDto.getSource());
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String deleteFavourites(@AuthenticationPrincipal User user) {
        favouriteService.deleteFavourites(user);
        return "redirect:/favourites";
    }

    @PostMapping("/send-email")
    public String sendEmail(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        String message;
        try {
            emailSenderService.sendEmailWithAttachment(user.getEmail(), "Favourites", "", EmailSenderService.attachment);
        } catch (MessagingException | FileNotFoundException e) {
            log.error(e.getMessage());
        }
        message = "Список избранных вакансий успешно отправлен!";
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/favourites";
    }
}
