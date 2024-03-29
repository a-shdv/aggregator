package com.company.aggregator.controllers;

import com.company.aggregator.dtos.FavouriteDto;
import com.company.aggregator.exceptions.favourite.FavouriteAlreadyExistsException;
import com.company.aggregator.exceptions.favourite.FavouriteNotFoundException;
import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
import com.company.aggregator.services.impl.FavouriteServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favourites")
@Slf4j
public class FavouriteController {
    private final FavouriteServiceImpl favouriteServiceImpl;

    @GetMapping
    public String findFavourites(@AuthenticationPrincipal User user,
                                 @RequestParam(required = false, defaultValue = "0") int page,
                                 @RequestParam(required = false, defaultValue = "5") int size,
                                 Model model) {
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        Page<Favourite> favourites = favouriteServiceImpl.findFavourites(user, PageRequest.of(page, size));
        model.addAttribute("favourites", favourites);
        return "favourites/favourites";
    }

    @PostMapping
    public ResponseEntity<String> addToFavourites(@AuthenticationPrincipal User user,
                                                  @ModelAttribute("favouriteDto") FavouriteDto favouriteDto) throws FavouriteAlreadyExistsException {
        favouriteServiceImpl.addToFavourites(user, FavouriteDto.toFavourite(favouriteDto));
        return ResponseEntity.ok("Success!");
    }


    @PostMapping("/{id}")
    public ResponseEntity<String> deleteFromFavourites(@AuthenticationPrincipal User user, @PathVariable Long id) throws FavouriteNotFoundException {
        favouriteServiceImpl.deleteFromFavourites(user, id);
        return ResponseEntity.ok().body("Success!");
    }

    @PostMapping("/clear")
    public ResponseEntity<String> deleteVacancies(@AuthenticationPrincipal User user) {
        favouriteServiceImpl.deleteAllFavourites(user);
        return ResponseEntity.ok().body("Vacancies cleared successfully");
    }
}
