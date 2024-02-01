package com.company.aggregator.controller;

import com.company.aggregator.dto.FavouriteDto;
import com.company.aggregator.exception.FavouriteAlreadyExistsException;
import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favourites")
@Slf4j
public class FavouriteController {
    private final FavouriteService favouriteService;

    @GetMapping
    public CompletableFuture<String> findFavourites(@AuthenticationPrincipal User user,
                                                    @RequestParam(required = false, defaultValue = "0") int page,
                                                    @RequestParam(required = false, defaultValue = "5") int size,
                                                    Model model) {
        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture<Page<Favourite>> favourites = favouriteService.findFavouritesAsync(user, PageRequest.of(page, size));
            model.addAttribute("favourites", favourites.join());
            return "users/favourites";
        });
    }

    @PostMapping
    public CompletableFuture<String> addToFavourites(@AuthenticationPrincipal User user,
                                                     @ModelAttribute("favouriteDto") FavouriteDto favouriteDto,
                                                     RedirectAttributes redirectAttributes) {
        return CompletableFuture.supplyAsync(() -> {
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
        });
    }

    @PostMapping("/clear")
    public CompletableFuture<String> deleteFavourites(@AuthenticationPrincipal User user) {
        return CompletableFuture.supplyAsync(() -> {
            favouriteService.deleteFavourites(user);
            return "redirect:/favourites";
        });
    }
}
