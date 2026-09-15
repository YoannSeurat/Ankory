package com.example.ankory.controllers;

import com.example.ankory.dto.MenuItemDto;
import com.example.ankory.services.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private static final Logger log = LoggerFactory.getLogger(RestaurantController.class);
    private final MenuService menuService;

    public RestaurantController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemDto>> getMenu(@PathVariable("id") Long id) {
        log.info("GET /restaurants/{}/menu", id);
        return ResponseEntity.ok(menuService.getMenuForRestaurant(id));
    }
}
