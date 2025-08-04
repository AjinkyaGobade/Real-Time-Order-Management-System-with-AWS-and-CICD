package com.example.orderservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private String customerName;
    private BigDecimal orderAmount;
    private LocalDate orderDate;
    private String invoiceFileUrl;
}