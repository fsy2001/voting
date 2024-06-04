package com.tuanyi.voting.service;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {
    @Value("${image-location}")
    private String directory;

    public String saveImage(MultipartFile image) throws IOException {
        String filename = UUID.randomUUID().toString();

        String extension = Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf("."));

        String finalPath = directory + filename + extension;

        Path path = Paths.get(finalPath);
        Files.copy(image.getInputStream(), path);

        return filename + extension;
    }

    public Resource loadImage(String imageName) throws IOException {
        Path root = Paths.get(directory);
        Path file = root.resolve(imageName);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Could not read file: " + imageName);
        }
    }
}