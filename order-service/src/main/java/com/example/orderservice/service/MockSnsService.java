package com.example.orderservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.orderservice.model.Order;

/**
 * Mock implementation of SnsService for local testing
 * This service logs notifications instead of sending them to AWS SNS
 */
@Service
@Profile("test")
@Primary
public class MockSnsService extends SnsService {

    private final List<String> sentNotifications = new ArrayList<>();

    public MockSnsService() {
        super(null, "mock-topic-arn");
    }

    @Override
    public void sendOrderNotification(Order order) {
        // Create notification message
        String message = String.format(
            "New order created: ID=%s, Customer=%s, Amount=%s, Date=%s",
            order.getOrderId(),
            order.getCustomerName(),
            order.getOrderAmount(),
            order.getOrderDate()
        );
        
        // Store notification in memory
        sentNotifications.add(message);
        
        // Log notification
        System.out.println("MOCK SNS NOTIFICATION: " + message);
    }
    
    /**
     * Get all sent notifications (for testing purposes)
     */
    public List<String> getSentNotifications() {
        return sentNotifications;
    }
}