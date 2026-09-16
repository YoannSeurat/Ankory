package com.example.ankory.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponseDto {
    public Long id;
    public Long restaurantId;
    public String status;
    public BigDecimal totalAmount;
    public Instant createdAt;
    public List<OrderLineResponseDto> lines;

    public static class OrderLineResponseDto {
        public Long menuItemId;
        public String name;
        public Integer quantity;
        public BigDecimal unitPrice;
    }
}
