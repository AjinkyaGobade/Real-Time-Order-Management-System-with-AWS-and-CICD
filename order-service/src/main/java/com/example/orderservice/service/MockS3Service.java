package com.example.orderservice.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Mock implementation of S3Service for local testing
 * This service stores files in memory instead of using AWS S3
 */
@Service
@Profile("test")
@Primary
public class MockS3Service extends S3Service {

    private final Map<String, byte[]> fileStorage = new HashMap<>();
    private final String bucketName = "mock-bucket";

    public MockS3Service() {
        super(null, "mock-bucket");
    }

    @Override
    public String uploadFile(String key, MultipartFile file) throws IOException {
        // Store file in memory
        fileStorage.put(key, file.getBytes());
        
        // Return a mock URL
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
    }

    @Override
    public byte[] downloadFile(String key) {
        // Retrieve file from memory
        byte[] fileContent = fileStorage.get(key);
        
        if (fileContent == null) {
            throw new RuntimeException("File not found: " + key);
        }
        
        return fileContent;
    }
}