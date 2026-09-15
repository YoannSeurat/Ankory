package com.example.ankory.services;

import com.example.ankory.dto.MenuItemDto;
import com.example.ankory.entities.MenuItem;
import com.example.ankory.exceptions.NotFoundException;
import com.example.ankory.repositories.MenuItemRepository;
import com.example.ankory.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<MenuItemDto> getMenuForRestaurant(Long restaurantId) {
        log.info("Fetching menu for restaurant {}", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new NotFoundException("Restaurant not found: " + restaurantId);
        }
        List<MenuItem> items = menuItemRepository.findByRestaurantId(restaurantId);
        return items.stream()
                .map(i -> new MenuItemDto(i.getId(), i.getName(), i.getDescription(), i.getPrice()))
                .collect(Collectors.toList());
    }
}
