package com.example.ankory.controllers;

import com.example.ankory.dto.MenuItemDto;
import com.example.ankory.dto.RestaurantDto;
import com.example.ankory.repositories.RestaurantRepository;
import com.example.ankory.services.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);
    private final MenuService menuService;
    private final RestaurantRepository restaurantRepository;

    public RestaurantController(MenuService menuService, RestaurantRepository restaurantRepository) {
        this.menuService = menuService;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> listRestaurants() {
        log.info("GET /restaurants");
        var list = restaurantRepository.findAll().stream()
                .map(r -> new RestaurantDto(r.getId(), r.getName(), r.getCuisine()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemDto>> getMenu(@PathVariable("id") Long id) {
        log.info("GET /restaurants/{}/menu", id);
        return ResponseEntity.ok(menuService.getMenuForRestaurant(id));
    }
}
