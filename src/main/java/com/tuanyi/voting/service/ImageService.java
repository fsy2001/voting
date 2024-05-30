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
    @Value("${image.location}")
    private String directory;

    public String saveImage(MultipartFile image) throws IOException {
        // Generate a random filename
        String filename = UUID.randomUUID().toString();

        // Get the extension of the original file
        String extension = Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf("."));

        // Concatenate the directory, filename, and extension
        // Specify your directory path here
        String finalPath = directory + filename + extension;

        // Create a Path instance
        Path path = Paths.get(finalPath);
        Files.copy(image.getInputStream(), path);

        // Return the filename
        return filename + extension;
    }

    public Resource loadImage(String imageName) throws IOException {
        Path root = Paths.get(directory);
        Path file = root.resolve(imageName);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file " + imageName);
        }
    }
}