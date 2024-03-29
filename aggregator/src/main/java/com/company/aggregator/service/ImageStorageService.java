package com.company.aggregator.service;

import com.company.aggregator.entity.Image;
import com.company.aggregator.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    Image uploadImage(MultipartFile multipartFile) throws IOException;

    byte[] downloadImage(String filename);

    void deleteAvatar(User user);
}
