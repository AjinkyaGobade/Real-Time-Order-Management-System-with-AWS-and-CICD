package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private DynamoDbService dynamoDbService;

    @Mock
    private S3Service s3Service;

    @Mock
    private SnsService snsService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrderWithoutInvoice() throws IOException {
        // Prepare test data
        String customerName = "Test Customer";
        String orderAmount = "150.50";
        String orderDate = LocalDate.now().toString();

        // Mock service methods
        doNothing().when(dynamoDbService).saveOrder(any(Order.class));
        doNothing().when(snsService).sendOrderNotification(any(Order.class));

        // Call the service method
        Order createdOrder = orderService.createOrder(customerName, orderAmount, orderDate, null);

        // Verify the result
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getOrderId());
        assertEquals(customerName, createdOrder.getCustomerName());
        assertEquals(new BigDecimal(orderAmount), createdOrder.getOrderAmount());
        assertEquals(LocalDate.parse(orderDate), createdOrder.getOrderDate());
        assertNull(createdOrder.getInvoiceFileUrl());

        // Verify interactions with mocked services
        verify(dynamoDbService, times(1)).saveOrder(any(Order.class));
        verify(snsService, times(1)).sendOrderNotification(any(Order.class));
        verify(s3Service, never()).uploadFile(anyString(), any(MultipartFile.class));
    }

    @Test
    public void testCreateOrderWithInvoice() throws IOException {
        // Prepare test data
        String customerName = "Test Customer";
        String orderAmount = "150.50";
        String orderDate = LocalDate.now().toString();
        MockMultipartFile invoiceFile = new MockMultipartFile(
                "invoice",
                "invoice.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );
        String fileUrl = "https://bucket.s3.amazonaws.com/invoices/123/invoice.pdf";

        // Mock service methods
        when(s3Service.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(fileUrl);
        doNothing().when(dynamoDbService).saveOrder(any(Order.class));
        doNothing().when(snsService).sendOrderNotification(any(Order.class));

        // Call the service method
        Order createdOrder = orderService.createOrder(customerName, orderAmount, orderDate, invoiceFile);

        // Verify the result
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getOrderId());
        assertEquals(customerName, createdOrder.getCustomerName());
        assertEquals(new BigDecimal(orderAmount), createdOrder.getOrderAmount());
        assertEquals(LocalDate.parse(orderDate), createdOrder.getOrderDate());
        assertEquals(fileUrl, createdOrder.getInvoiceFileUrl());

        // Verify interactions with mocked services
        verify(s3Service, times(1)).uploadFile(anyString(), any(MultipartFile.class));
        verify(dynamoDbService, times(1)).saveOrder(any(Order.class));
        verify(snsService, times(1)).sendOrderNotification(any(Order.class));
    }

    @Test
    public void testGetOrder() {
        // Prepare test data
        String orderId = "123";
        Order expectedOrder = new Order(orderId, "Customer", new BigDecimal("100.00"), LocalDate.now(), null);

        // Mock service method
        when(dynamoDbService.getOrder(orderId)).thenReturn(expectedOrder);

        // Call the service method
        Order result = orderService.getOrder(orderId);

        // Verify the result
        assertNotNull(result);
        assertEquals(expectedOrder, result);

        // Verify interaction with mocked service
        verify(dynamoDbService, times(1)).getOrder(orderId);
    }

    @Test
    public void testGetAllOrders() {
        // Prepare test data
        List<Order> expectedOrders = Arrays.asList(
                new Order("1", "Customer 1", new BigDecimal("100.00"), LocalDate.now(), null),
                new Order("2", "Customer 2", new BigDecimal("200.00"), LocalDate.now(), null)
        );

        // Mock service method
        when(dynamoDbService.getAllOrders()).thenReturn(expectedOrders);

        // Call the service method
        List<Order> result = orderService.getAllOrders();

        // Verify the result
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedOrders, result);

        // Verify interaction with mocked service
        verify(dynamoDbService, times(1)).getAllOrders();
    }

    @Test
    public void testGetInvoice() throws IOException {
        // Prepare test data
        String orderId = "123";
        String fileUrl = "https://bucket.s3.amazonaws.com/invoices/123/invoice.pdf";
        Order order = new Order(orderId, "Customer", new BigDecimal("100.00"), LocalDate.now(), fileUrl);
        byte[] expectedFileContent = "PDF content".getBytes();

        // Mock service methods
        when(dynamoDbService.getOrder(orderId)).thenReturn(order);
        when(s3Service.downloadFile(anyString())).thenReturn(expectedFileContent);

        // Call the service method
        byte[] result = orderService.getInvoice(orderId);

        // Verify the result
        assertNotNull(result);
        assertArrayEquals(expectedFileContent, result);

        // Verify interactions with mocked services
        verify(dynamoDbService, times(1)).getOrder(orderId);
        verify(s3Service, times(1)).downloadFile(anyString());
    }

    @Test
    public void testGetInvoiceOrderNotFound() {
        // Prepare test data
        String orderId = "999";

        // Mock service method
        when(dynamoDbService.getOrder(orderId)).thenReturn(null);

        // Call the service method and verify exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.getInvoice(orderId);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Invoice not found for order"));

        // Verify interaction with mocked service
        verify(dynamoDbService, times(1)).getOrder(orderId);
        verify(s3Service, never()).downloadFile(anyString());
    }
}