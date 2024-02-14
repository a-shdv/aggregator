package com.company.aggregator.controller;

import com.company.aggregator.dto.FavouriteDto;
import com.company.aggregator.exception.FavouriteAlreadyExistsException;
import com.company.aggregator.exception.FavouritesIsEmptyException;
import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.service.EmailSenderService;
import com.company.aggregator.service.FavouriteService;
import com.company.aggregator.service.PdfGeneratorService;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@RequestMapping("/favourites")
@Slf4j
public class FavouriteController {
    private final FavouriteService favouriteService;
    private final EmailSenderService emailSenderService;
    private final PdfGeneratorService pdfGeneratorService;

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

    @PostMapping("/generate-pdf")
    public String generatePdf(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        String message;
        String pdfPath = System.getProperty("user.home") + "/Downloads/report-" + UUID.randomUUID() + ".pdf";
        try {
            List<Favourite> favourites = favouriteService.findByUser(user).join();
            pdfGeneratorService.generatePdf(favourites, pdfPath);
            emailSenderService.sendEmailWithAttachment(user.getEmail(), "Избранные вакансии", "", pdfPath);
            message = "Pdf успешно сгенерирован и отправлен на почту!";
        } catch (MessagingException | FileNotFoundException | FavouritesIsEmptyException e) {
            message = e.getMessage();
        }
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/favourites";
    }
}
