package com.example.biblio.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Component

public interface FileUploadManager {
    String uploadFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    String uploadImage(MultipartFile image);
    String storeFile(MultipartFile file, Path directory);
    Resource loadImageAsResource(String fileName);
    Resource loadResource(String fileName, Path directory);



    }
