package com.company.aggregator.services;

import com.company.aggregator.models.Image;
import com.company.aggregator.models.User;
import com.company.aggregator.repositories.ImageStorageRepository;
import com.company.aggregator.repositories.UserRepository;
import com.company.aggregator.utils.ImageCompressor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    private final UserRepository userRepository;
    private final ImageStorageRepository imageStorageRepository;

    @Transactional
    public void deleteAvatar(User user) {
        Optional<Image> image = imageStorageRepository.findByUser(user);
        user.setAvatar(null);
        userRepository.save(user);
        image.ifPresent(imageStorageRepository::delete);
    }

    @Transactional
    public Image uploadImage(MultipartFile multipartFile) throws IOException {
        Optional<Image> image = imageStorageRepository.findByName(multipartFile.getOriginalFilename());
        if (image.isPresent()) {
            return image.get();
        }
        return imageStorageRepository.save(Image.builder()
                .name(multipartFile.getOriginalFilename())
                .type(multipartFile.getContentType())
                .data(ImageCompressor.compressImage(multipartFile.getBytes()))
                .build());
    }

    @Transactional
    public byte[] downloadImage(String filename) {
        Optional<Image> image = imageStorageRepository.findByName(filename);
        return ImageCompressor.decompressImage(image.get().getData()); // binary data
    }
}
