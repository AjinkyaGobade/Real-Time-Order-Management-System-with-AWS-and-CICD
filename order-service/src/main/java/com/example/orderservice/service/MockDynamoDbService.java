package com.example.orderservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.example.orderservice.model.Order;

/**
 * Mock implementation of DynamoDbService for local testing
 * This service stores orders in memory instead of using AWS DynamoDB
 */
@Service
@Profile("test")
@Primary
public class MockDynamoDbService extends DynamoDbService {

    private final Map<String, Order> orderStorage = new HashMap<>();

    public MockDynamoDbService() {
        super(null, "mock-table");
    }

    @Override
    public void saveOrder(Order order) {
        // Log the order being saved
        System.out.println("MOCK DYNAMODB: Saving order with ID: " + order.getOrderId());
        
        // Store order in memory
        orderStorage.put(order.getOrderId(), order);
        
        // Log success
        System.out.println("MOCK DYNAMODB: Order saved successfully");
    }

    @Override
    public Order getOrder(String orderId) {
        // Log the order ID being requested
        System.out.println("MOCK DYNAMODB: Getting order with ID: " + orderId);
        
        // Retrieve order from memory
        Order order = orderStorage.get(orderId);
        
        if (order == null) {
            System.out.println("MOCK DYNAMODB: Order not found: " + orderId);
            // Return null instead of throwing exception to match DynamoDbService behavior
            return null;
        }
        
        System.out.println("MOCK DYNAMODB: Order found: " + order);
        return order;
    }

    @Override
    public List<Order> getAllOrders() {
        // Log the number of orders in storage
        System.out.println("MOCK DYNAMODB: Getting all orders, count: " + orderStorage.size());
        
        // Return all orders from memory
        return new ArrayList<>(orderStorage.values());
    }
}