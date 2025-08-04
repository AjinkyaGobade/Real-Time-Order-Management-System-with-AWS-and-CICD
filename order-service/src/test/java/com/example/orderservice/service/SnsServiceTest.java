package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SnsServiceTest {

    @Mock
    private SnsClient snsClient;

    private SnsService snsService;

    private final String topicArn = "arn:aws:sns:us-east-1:123456789012:test-topic";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        snsService = new SnsService(snsClient, topicArn);
    }

    @Test
    public void testSendOrderNotification() {
        // Prepare test data
        Order order = new Order(
                "123",
                "Test Customer",
                new BigDecimal("150.50"),
                LocalDate.now(),
                "https://bucket.s3.amazonaws.com/invoices/123/invoice.pdf"
        );

        // Mock SNS client response
        when(snsClient.publish(any(PublishRequest.class)))
                .thenReturn(PublishResponse.builder().messageId("message-id").build());

        // Call the service method
        snsService.sendOrderNotification(order);

        // Capture the request argument
        ArgumentCaptor<PublishRequest> requestCaptor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(snsClient, times(1)).publish(requestCaptor.capture());

        // Verify the request
        PublishRequest capturedRequest = requestCaptor.getValue();
        assertEquals(topicArn, capturedRequest.topicArn());
        assertEquals("New Order Notification", capturedRequest.subject());
        
        // Verify message content
        String message = capturedRequest.message();
        assertTrue(message.contains("Order ID: " + order.getOrderId()));
        assertTrue(message.contains("Customer: " + order.getCustomerName()));
        assertTrue(message.contains("Amount: $" + order.getOrderAmount()));
        assertTrue(message.contains("Date: " + order.getOrderDate()));
    }
}