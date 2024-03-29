package com.company.aggregator.service.impl;

import com.company.aggregator.entity.Image;
import com.company.aggregator.entity.User;
import com.company.aggregator.repository.ImageStorageRepository;
import com.company.aggregator.repository.UserRepository;
import com.company.aggregator.service.ImageStorageService;
import com.company.aggregator.util.ImageCompressor;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageStorageServiceImpl implements ImageStorageService {
    UserRepository userRepository;
    ImageStorageRepository imageStorageRepository;

    @Override
    @Transactional
    public Image uploadImage(MultipartFile multipartFile) throws IOException {
        Optional<Image> image = imageStorageRepository.findImageByFilename(multipartFile.getOriginalFilename());
        if (image.isPresent()) {
            return image.get();
        }
        return imageStorageRepository.save(Image.builder()
                .filename(multipartFile.getOriginalFilename())
                .type(multipartFile.getContentType())
                .data(ImageCompressor.compressImage(multipartFile.getBytes()))
                .build());
    }

    @Override
    @Transactional
    public byte[] downloadImage(String filename) {
        Optional<Image> image = imageStorageRepository.findImageByFilename(filename);
        return ImageCompressor.decompressImage(image.get().getData()); // binary data
    }

    @Override
    @Transactional
    public void deleteAvatar(User user) {
        Optional<Image> image = imageStorageRepository.findImageByUser(user);
        user.setAvatar(null);
        userRepository.save(user);
        image.ifPresent(imageStorageRepository::delete);
    }
}
