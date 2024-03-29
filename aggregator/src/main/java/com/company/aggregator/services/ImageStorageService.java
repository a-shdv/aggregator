package com.company.aggregator.services;

import com.company.aggregator.models.Image;
import com.company.aggregator.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    Image uploadImage(MultipartFile multipartFile) throws IOException;

    byte[] downloadImage(String filename);

    void deleteAvatar(User user);
}
