package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DynamoDbServiceTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    private DynamoDbService dynamoDbService;

    private final String tableName = "test-orders";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        dynamoDbService = new DynamoDbService(dynamoDbClient, tableName);
    }

    @Test
    public void testSaveOrder() {
        // Prepare test data
        Order order = new Order(
                "123",
                "Test Customer",
                new BigDecimal("150.50"),
                LocalDate.now(),
                "https://bucket.s3.amazonaws.com/invoices/123/invoice.pdf"
        );

        // Mock DynamoDB client response
        when(dynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(PutItemResponse.builder().build());

        // Call the service method
        dynamoDbService.saveOrder(order);

        // Capture the request argument
        ArgumentCaptor<PutItemRequest> requestCaptor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(dynamoDbClient, times(1)).putItem(requestCaptor.capture());

        // Verify the request
        PutItemRequest capturedRequest = requestCaptor.getValue();
        assertEquals(tableName, capturedRequest.tableName());

        // Verify the item attributes
        Map<String, AttributeValue> item = capturedRequest.item();
        assertEquals(order.getOrderId(), item.get("orderId").s());
        assertEquals(order.getCustomerName(), item.get("customerName").s());
        assertEquals(order.getOrderAmount().toString(), item.get("orderAmount").n());
        assertEquals(order.getOrderDate().toString(), item.get("orderDate").s());
        assertEquals(order.getInvoiceFileUrl(), item.get("invoiceFileUrl").s());
    }

    @Test
    public void testGetOrder() {
        // Prepare test data
        String orderId = "123";
        LocalDate orderDate = LocalDate.now();
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("orderId", AttributeValue.builder().s(orderId).build());
        item.put("customerName", AttributeValue.builder().s("Test Customer").build());
        item.put("orderAmount", AttributeValue.builder().n("150.50").build());
        item.put("orderDate", AttributeValue.builder().s(orderDate.toString()).build());
        item.put("invoiceFileUrl", AttributeValue.builder().s("https://bucket.s3.amazonaws.com/invoices/123/invoice.pdf").build());

        // Mock DynamoDB client response
        when(dynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().item(item).build());

        // Call the service method
        Order result = dynamoDbService.getOrder(orderId);

        // Verify the result
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals(new BigDecimal("150.50"), result.getOrderAmount());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals("https://bucket.s3.amazonaws.com/invoices/123/invoice.pdf", result.getInvoiceFileUrl());

        // Capture the request argument
        ArgumentCaptor<GetItemRequest> requestCaptor = ArgumentCaptor.forClass(GetItemRequest.class);
        verify(dynamoDbClient, times(1)).getItem(requestCaptor.capture());

        // Verify the request
        GetItemRequest capturedRequest = requestCaptor.getValue();
        assertEquals(tableName, capturedRequest.tableName());
        assertEquals(orderId, capturedRequest.key().get("orderId").s());
    }

    @Test
    public void testGetOrderNotFound() {
        // Prepare test data
        String orderId = "999";

        // Mock DynamoDB client response (empty item)
        when(dynamoDbClient.getItem(any(GetItemRequest.class)))
                .thenReturn(GetItemResponse.builder().build());

        // Call the service method
        Order result = dynamoDbService.getOrder(orderId);

        // Verify the result is null
        assertNull(result);

        // Verify interaction with DynamoDB client
        verify(dynamoDbClient, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test
    public void testGetAllOrders() {
        // Prepare test data
        LocalDate orderDate = LocalDate.now();
        Map<String, AttributeValue> item1 = new HashMap<>();
        item1.put("orderId", AttributeValue.builder().s("1").build());
        item1.put("customerName", AttributeValue.builder().s("Customer 1").build());
        item1.put("orderAmount", AttributeValue.builder().n("100.00").build());
        item1.put("orderDate", AttributeValue.builder().s(orderDate.toString()).build());

        Map<String, AttributeValue> item2 = new HashMap<>();
        item2.put("orderId", AttributeValue.builder().s("2").build());
        item2.put("customerName", AttributeValue.builder().s("Customer 2").build());
        item2.put("orderAmount", AttributeValue.builder().n("200.00").build());
        item2.put("orderDate", AttributeValue.builder().s(orderDate.toString()).build());
        item2.put("invoiceFileUrl", AttributeValue.builder().s("https://bucket.s3.amazonaws.com/invoices/2/invoice.pdf").build());

        // Mock DynamoDB client response
        when(dynamoDbClient.scan(any(ScanRequest.class)))
                .thenReturn(ScanResponse.builder().items(item1, item2).build());

        // Call the service method
        List<Order> results = dynamoDbService.getAllOrders();

        // Verify the results
        assertNotNull(results);
        assertEquals(2, results.size());

        // Verify first order
        Order order1 = results.get(0);
        assertEquals("1", order1.getOrderId());
        assertEquals("Customer 1", order1.getCustomerName());
        assertEquals(new BigDecimal("100.00"), order1.getOrderAmount());
        assertEquals(orderDate, order1.getOrderDate());
        assertNull(order1.getInvoiceFileUrl());

        // Verify second order
        Order order2 = results.get(1);
        assertEquals("2", order2.getOrderId());
        assertEquals("Customer 2", order2.getCustomerName());
        assertEquals(new BigDecimal("200.00"), order2.getOrderAmount());
        assertEquals(orderDate, order2.getOrderDate());
        assertEquals("https://bucket.s3.amazonaws.com/invoices/2/invoice.pdf", order2.getInvoiceFileUrl());

        // Verify interaction with DynamoDB client
        ArgumentCaptor<ScanRequest> requestCaptor = ArgumentCaptor.forClass(ScanRequest.class);
        verify(dynamoDbClient, times(1)).scan(requestCaptor.capture());

        // Verify the request
        ScanRequest capturedRequest = requestCaptor.getValue();
        assertEquals(tableName, capturedRequest.tableName());
    }
}