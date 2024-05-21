package com.example.biblio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService implements FileUploadManager {

    private final Path uploadDir;
    private final Path imageUploadDir;
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);


    public FileUploadService(@Value("${file.upload-dir}") String uploadDir,
                             @Value("${file.image-upload-dir}") String imageUploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.imageUploadDir = Paths.get(imageUploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            Files.createDirectories(this.imageUploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return storeFile(file, this.uploadDir);
    }
    @Override

    public String uploadImage(MultipartFile image) {
        return storeFile(image, this.imageUploadDir);
    }
    @Override
    public String storeFile(MultipartFile file, Path directory) {
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return fileName;
        } catch (IOException e) {
            logger.error("Error uploading file", e);
            return null;
        }
    }
    @Override

    public Resource loadFileAsResource(String fileName) {
        return loadResource(fileName, this.uploadDir);
    }
    @Override

    public Resource loadImageAsResource(String fileName) {
        return loadResource(fileName, this.imageUploadDir);
    }
    @Override
    public Resource loadResource(String fileName, Path directory) {
        try {
            Path filePath = directory.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException | FileNotFoundException ex) {
            throw new RuntimeException("File not found or invalid path: " + fileName, ex);
        }
    }
}
