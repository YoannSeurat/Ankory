package com.example.ankory.config;

import com.example.ankory.entities.MenuItem;
import com.example.ankory.entities.Restaurant;
import com.example.ankory.repositories.MenuItemRepository;
import com.example.ankory.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public DataLoader(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (restaurantRepository.count() > 0) return;
        log.info("Seeding sample restaurants and menu items");
        Restaurant r1 = new Restaurant("Le Bon Chef", "French");
        Restaurant r2 = new Restaurant("Pizza Planet", "Italian");
        restaurantRepository.save(r1);
        restaurantRepository.save(r2);

        menuItemRepository.save(new MenuItem(r1, "Quiche Lorraine", "Classic quiche", new BigDecimal("8.50")));
        menuItemRepository.save(new MenuItem(r1, "Croque Monsieur", "Ham & cheese", new BigDecimal("6.00")));
        menuItemRepository.save(new MenuItem(r2, "Margherita", "Tomato, mozzarella", new BigDecimal("9.00")));
        menuItemRepository.save(new MenuItem(r2, "Pepperoni", "Spicy salami", new BigDecimal("10.50")));
    }
}
