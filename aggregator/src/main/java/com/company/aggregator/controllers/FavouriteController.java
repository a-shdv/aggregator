package com.company.aggregator.controllers;

import com.company.aggregator.dtos.FavouriteDto;
import com.company.aggregator.exceptions.FavouriteAlreadyExistsException;
import com.company.aggregator.exceptions.FavouriteNotFoundException;
import com.company.aggregator.exceptions.FavouritesIsEmptyException;
import com.company.aggregator.models.Favourite;
import com.company.aggregator.models.User;
import com.company.aggregator.services.EmailSenderService;
import com.company.aggregator.services.FavouriteService;
import com.company.aggregator.services.PdfGeneratorService;
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
        String success = (String) model.getAttribute("success");
        String error = (String) model.getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        CompletableFuture<Page<Favourite>> favourites = favouriteService.findFavouritesAsync(user, PageRequest.of(page, size)); // TODO вылетает
        model.addAttribute("favourites", favourites.join());
        return "vacancies/favourites";
    }

    @PostMapping
    public String addToFavourites(@AuthenticationPrincipal User user,
                                  @ModelAttribute("favouriteDto") FavouriteDto favouriteDto,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (favouriteService.findBySourceAsync(favouriteDto.getSource()).join() != null) {
                throw new FavouriteAlreadyExistsException("Вакансия уже существует в избранном " + favouriteDto.getSource());
            }
            favouriteService.addToFavouritesAsync(user, FavouriteDto.toFavourite(favouriteDto));
        } catch (FavouriteAlreadyExistsException e) {
            log.info(e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        redirectAttributes.addFlashAttribute("success", "Вакансия была добавлена в избранное " + favouriteDto.getSource());
        return "redirect:/vacancies";
    }

    @PostMapping("/{id}")
    public String deleteFromFavourites(@AuthenticationPrincipal User user, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            favouriteService.deleteFromFavourites(user, id);
            redirectAttributes.addFlashAttribute("success", "Вакансия успешно удалена!");
        } catch (FavouriteNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/favourites";
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
//            emailSenderService.sendEmailWithAttachment(user.getEmail(), "Избранные вакансии", "", pdfPath);
            emailSenderService.sendEmailWithAttachment("shadaev2001@icloud.com", "Избранные вакансии", "", pdfPath);
            message = "Pdf успешно сгенерирован и отправлен на почту!";
        } catch (MessagingException | FileNotFoundException | FavouritesIsEmptyException e) {
            message = e.getMessage();
        }
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/favourites";
    }
}
