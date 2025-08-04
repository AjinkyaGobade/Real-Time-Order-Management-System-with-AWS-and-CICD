package com.example.orderservice.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.orderservice.model.Order;

@Service
public class OrderService {

    private final DynamoDbService dynamoDbService;
    private final S3Service s3Service;
    private final SnsService snsService;

    public OrderService(DynamoDbService dynamoDbService, S3Service s3Service, SnsService snsService) {
        this.dynamoDbService = dynamoDbService;
        this.s3Service = s3Service;
        this.snsService = snsService;
    }

    public Order createOrder(String customerName, String orderAmount, String orderDate, MultipartFile invoiceFile) throws IOException {
        // Generate a unique order ID
        String orderId = UUID.randomUUID().toString();
        
        // Build the order object
        Order order = Order.builder()
                .orderId(orderId)
                .customerName(customerName)
                .orderAmount(new java.math.BigDecimal(orderAmount))
                .orderDate(LocalDate.parse(orderDate))
                .build();
        
        // Upload invoice file to S3 if provided
        if (invoiceFile != null && !invoiceFile.isEmpty()) {
            String key = String.format("invoices/%s/%s", orderId, invoiceFile.getOriginalFilename());
            String fileUrl = s3Service.uploadFile(key, invoiceFile);
            order.setInvoiceFileUrl(fileUrl);
        }
        
        // Save order to DynamoDB
        dynamoDbService.saveOrder(order);
        
        // Send notification via SNS
        snsService.sendOrderNotification(order);
        
        return order;
    }

    public Order getOrder(String orderId) {
        return dynamoDbService.getOrder(orderId);
    }

    public List<Order> getAllOrders() {
        return dynamoDbService.getAllOrders();
    }

    public byte[] getInvoice(String orderId) throws IOException {
        Order order = dynamoDbService.getOrder(orderId);
        if (order == null || order.getInvoiceFileUrl() == null) {
            throw new RuntimeException("Invoice not found for order: " + orderId);
        }
        
        // Extract the key from the URL
        String url = order.getInvoiceFileUrl();
        String key = url.substring(url.indexOf(".com/") + 5);
        
        return s3Service.downloadFile(key);
    }
}