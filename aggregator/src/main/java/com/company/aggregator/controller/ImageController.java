package com.company.aggregator.controller;

import com.company.aggregator.entity.Image;
import com.company.aggregator.entity.User;
import com.company.aggregator.service.ImageStorageService;
import com.company.aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final ImageStorageService imageStorageService;
    private final UserService userService;

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("image") MultipartFile file, @AuthenticationPrincipal User user) throws IOException {
        imageStorageService.deleteAvatar(user);
        Image image = imageStorageService.uploadImage(file);
        userService.uploadAvatar(user, image);
        return "redirect:/account-info";
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        Image image = imageStorageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Image has been successfully uploaded!");
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
        byte[] imageData = imageStorageService.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }
}
