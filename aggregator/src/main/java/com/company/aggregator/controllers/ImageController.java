package com.company.aggregator.controllers;

import com.company.aggregator.models.Image;
import com.company.aggregator.models.User;
import com.company.aggregator.services.impl.ImageStorageServiceImpl;
import com.company.aggregator.services.impl.UserServiceImpl;
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
    private final ImageStorageServiceImpl imageStorageServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("image") MultipartFile file, @AuthenticationPrincipal User user) throws IOException {
        imageStorageServiceImpl.deleteAvatar(user);
        Image image = imageStorageServiceImpl.uploadImage(file);
        userServiceImpl.uploadAvatar(user, image);
        return "redirect:/account-info";
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        Image image = imageStorageServiceImpl.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Image has been successfully uploaded!");
    }

    @GetMapping("/{fileName}")
    // @RestController
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
        byte[] imageData = imageStorageServiceImpl.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }
}
