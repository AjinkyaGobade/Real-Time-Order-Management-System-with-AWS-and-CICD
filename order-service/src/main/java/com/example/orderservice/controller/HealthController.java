package com.example.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple health check controller to verify the application is running
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Application is running";
    }
}