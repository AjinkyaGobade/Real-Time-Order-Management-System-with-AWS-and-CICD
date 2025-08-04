package com.example.orderservice.controller;

import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testGetAllOrders() throws Exception {
        // Prepare test data
        Order order1 = new Order("1", "Customer 1", new BigDecimal("100.00"), LocalDate.now(), null);
        Order order2 = new Order("2", "Customer 2", new BigDecimal("200.00"), LocalDate.now(), null);
        List<Order> orders = Arrays.asList(order1, order2);

        // Mock service method
        when(orderService.getAllOrders()).thenReturn(orders);

        // Perform GET request and validate response
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("1"))
                .andExpect(jsonPath("$[0].customerName").value("Customer 1"))
                .andExpect(jsonPath("$[1].orderId").value("2"))
                .andExpect(jsonPath("$[1].customerName").value("Customer 2"));
    }

    @Test
    public void testGetOrder() throws Exception {
        // Prepare test data
        Order order = new Order("1", "Customer 1", new BigDecimal("100.00"), LocalDate.now(), null);

        // Mock service method
        when(orderService.getOrder("1")).thenReturn(order);

        // Perform GET request and validate response
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("1"))
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }

    @Test
    public void testGetOrderNotFound() throws Exception {
        // Mock service method to return null (order not found)
        when(orderService.getOrder("999")).thenReturn(null);

        // Perform GET request and validate 404 response
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOrder() throws Exception {
        // Prepare test data
        Order createdOrder = new Order("1", "Customer 1", new BigDecimal("100.00"), LocalDate.now(), null);
        MockMultipartFile invoiceFile = new MockMultipartFile(
                "invoiceFile",
                "invoice.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "PDF content".getBytes()
        );

        // Mock service method
        when(orderService.createOrder(anyString(), anyString(), anyString(), any()))
                .thenReturn(createdOrder);

        // Perform POST request and validate response
        mockMvc.perform(multipart("/orders")
                .file(invoiceFile)
                .param("customerName", "Customer 1")
                .param("orderAmount", "100.00")
                .param("orderDate", LocalDate.now().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("1"))
                .andExpect(jsonPath("$.customerName").value("Customer 1"));
    }
}