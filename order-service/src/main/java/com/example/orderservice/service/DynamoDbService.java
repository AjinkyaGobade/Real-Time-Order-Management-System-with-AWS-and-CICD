package com.example.orderservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.orderservice.model.Order;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

@Service
public class DynamoDbService {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbService(DynamoDbClient dynamoDbClient, @Value("${aws.dynamodb.tableName}") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public void saveOrder(Order order) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("orderId", AttributeValue.builder().s(order.getOrderId()).build());
        item.put("customerName", AttributeValue.builder().s(order.getCustomerName()).build());
        item.put("orderAmount", AttributeValue.builder().n(order.getOrderAmount().toString()).build());
        item.put("orderDate", AttributeValue.builder().s(order.getOrderDate().toString()).build());
        
        if (order.getInvoiceFileUrl() != null) {
            item.put("invoiceFileUrl", AttributeValue.builder().s(order.getInvoiceFileUrl()).build());
        }

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

    public Order getOrder(String orderId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("orderId", AttributeValue.builder().s(orderId).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(getItemRequest).item();
        
        if (item == null || item.isEmpty()) {
            return null;
        }

        return mapToOrder(item);
    }

    public java.util.List<Order> getAllOrders() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        return dynamoDbClient.scan(scanRequest)
                .items()
                .stream()
                .map(this::mapToOrder)
                .collect(java.util.stream.Collectors.toList());
    }

    private Order mapToOrder(Map<String, AttributeValue> item) {
        Order order = new Order();
        order.setOrderId(item.get("orderId").s());
        order.setCustomerName(item.get("customerName").s());
        order.setOrderAmount(new java.math.BigDecimal(item.get("orderAmount").n()));
        order.setOrderDate(java.time.LocalDate.parse(item.get("orderDate").s()));
        
        if (item.containsKey("invoiceFileUrl")) {
            order.setInvoiceFileUrl(item.get("invoiceFileUrl").s());
        }
        
        return order;
    }
}