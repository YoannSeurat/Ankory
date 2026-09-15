package com.example.ankory.dto;

import java.math.BigDecimal;

public class MenuItemDto {
    public Long id;
    public String name;
    public String category;
    public String description;
    public BigDecimal price;

    public MenuItemDto() {}

    public MenuItemDto(Long id, String name, String category, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
    }
}
