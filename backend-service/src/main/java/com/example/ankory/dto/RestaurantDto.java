package com.example.ankory.dto;

public class RestaurantDto {
    public Long id;
    public String name;

    public RestaurantDto() {}

    public RestaurantDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
