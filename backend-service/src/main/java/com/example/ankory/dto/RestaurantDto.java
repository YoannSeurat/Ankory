package com.example.ankory.dto;

public class RestaurantDto {
    public Long id;
    public String name;
    public String cuisine;

    public RestaurantDto() {}

    public RestaurantDto(Long id, String name, String cuisine) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
    }
}
