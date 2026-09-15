package com.example.ankory.dto;

import java.util.List;

public class OrderRequestDto {
    public Long restaurantId;
    public List<OrderLineRequestDto> lines;

    public OrderRequestDto() {}
}
