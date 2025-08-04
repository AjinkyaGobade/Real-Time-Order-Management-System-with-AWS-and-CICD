package com.example.orderservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.orderservice.model.Order;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsService {

    private final SnsClient snsClient;
    private final String topicArn;

    public SnsService(SnsClient snsClient, @Value("${aws.sns.topicArn}") String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn;
    }

    public void sendOrderNotification(Order order) {
        String message = String.format(
                "New order created:\n" +
                "Order ID: %s\n" +
                "Customer: %s\n" +
                "Amount: $%s\n" +
                "Date: %s",
                order.getOrderId(),
                order.getCustomerName(),
                order.getOrderAmount().toString(),
                order.getOrderDate().toString());

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .subject("New Order Notification")
                .message(message)
                .build();

        snsClient.publish(publishRequest);
    }
}