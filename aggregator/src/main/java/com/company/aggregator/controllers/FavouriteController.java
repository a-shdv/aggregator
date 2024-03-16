package com.company.aggregator.controllers;

import com.company.aggregator.dtos.FavouriteDto;
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
        Page<Favourite> favourites = favouriteService.findFavouritesAsync(user, PageRequest.of(page, size)).join();
        model.addAttribute("favourites", favourites);
        return "vacancies/favourites";
    }

    @PostMapping
    public String addToFavourites(@AuthenticationPrincipal User user,
                                  @ModelAttribute("favouriteDto") FavouriteDto favouriteDto,
                                  RedirectAttributes redirectAttributes) {
        CompletableFuture<Void> future = favouriteService.addToFavouritesAsync(user, FavouriteDto.toFavourite(favouriteDto));
        future.handle((res, ex) -> {
            if (ex != null) {
                log.info(ex.getMessage());
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("success", "Вакансия была успешно добавлена в избранное!");
            }
            return null;
        }).join();
        return "redirect:/vacancies";
    }


    @PostMapping("/{id}")
    public String deleteFromFavourites(@AuthenticationPrincipal User user, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        CompletableFuture<Void> future = favouriteService.deleteFromFavouritesAsync(user, id);
        future.handle((res, ex) -> {
            if (ex != null) {
                log.info(ex.getMessage());
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("success", "Вакансия успешно удалена!");
            }
            return null;
        }).join();
        return "redirect:/favourites";
    }

    @PostMapping("/clear")
    public String deleteFavourites(@AuthenticationPrincipal User user) {
        favouriteService.deleteFavouritesAsync(user).join();
        return "redirect:/favourites";
    }

//    @PostMapping("/generate-pdf-and-send-to-email")
//    public String generatePdfAndSendToEmail(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        String pdfPath = System.getProperty("user.home") + "/Downloads/report-" + UUID.randomUUID() + ".pdf";
//
//        CompletableFuture.supplyAsync(() -> {
//                    List<Favourite> favourites = null;
//                    try {
//                        favourites = favouriteService.findByUser(user);
//                    } catch (FavouritesIsEmptyException e) {
//                        future.completeExceptionally(e);
//                    }
//
//                    return favourites;
//                })
//                .thenAccept((favourites) -> pdfGeneratorService
//                        .generatePdf(favourites, pdfPath))
//                .thenRun(() -> {
//                    try {
//                        emailSenderService.sendEmailWithAttachment("shadaev2001@icloud.com", "Избранные вакансии", "", pdfPath);
//                        future.complete(null);
//                    } catch (MessagingException | FileNotFoundException e) {
//                        future.completeExceptionally(e);
//                    }
//                });
//
//
//        future.handle((res, ex) -> {
//            if (ex != null) {
//                redirectAttributes.addFlashAttribute("error", ex.getMessage());
//            } else {
//                redirectAttributes.addFlashAttribute("success", "Pdf успешно сгенерирован и отправлен на почту!");
//            }
//            return null;
//        }).join();
//
//        return "redirect:/favourites";
//    }
}
