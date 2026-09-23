package com.example.ankory.config;

import com.example.ankory.entities.MenuItem;
import com.example.ankory.entities.Restaurant;
import com.example.ankory.repositories.MenuItemRepository;
import com.example.ankory.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

        ClassPathResource res = new ClassPathResource("config/data.csv");
        if (res.exists()) {
            log.info("Loading data from config/data.csv");
            try (InputStream is = res.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line = br.readLine(); // header
                Map<String, Restaurant> restaurants = new HashMap<>();
                while ((line = br.readLine()) != null) {
                    if (line.isBlank()) continue;
                    // simple CSV: restaurantName,menuName,category,price,description
                    String[] parts = parseCsvLine(line);
                                        if (parts.length < 5) {
                        log.warn("Skipping malformed line: {}", line);
                        continue;
                    }
                    String rName = parts[0].trim();
                    String menuName = parts[1].trim();
                    String category = parts[2].trim();
                    String priceStr = parts[3].trim();
                    String desc = parts[4].trim();
                    BigDecimal price;
                    try {
                        price = new BigDecimal(priceStr);
                    } catch (Exception _) {
                        log.warn("Invalid price in record, skipping: {}", line);
                        continue;
                    }
                    Restaurant r = restaurants.get(rName);
                    if (r == null) {
                        r = new Restaurant(rName);
                        restaurantRepository.save(r);
                        restaurants.put(rName, r);
                    }
                    MenuItem mi = new MenuItem(r, menuName, category, desc, price);
                    menuItemRepository.save(mi);
                }
            } catch (Exception e) {
                log.error("Failed to load config/data.csv, falling back to default seeds", e);
                seedDefaults();
            }
        } else {
            log.info("config/data.csv not found, seeding defaults");
            seedDefaults();
        }
    }

    private void seedDefaults() {
        Restaurant r1 = new Restaurant("Big Chef");
        Restaurant r2 = new Restaurant("Italy World");
        restaurantRepository.save(r1);
        restaurantRepository.save(r2);

        menuItemRepository.save(new MenuItem(r1, "Quiche Lorraine", "Plats", "Classic quiche", new BigDecimal("8.50")));
        menuItemRepository.save(new MenuItem(r1, "Croque Monsieur", "Plats", "Ham & cheese", new BigDecimal("6.00")));
        menuItemRepository.save(new MenuItem(r2, "Margherita", "Plats", "Tomato, mozzarella", new BigDecimal("9.00")));
        menuItemRepository.save(new MenuItem(r2, "Pepperoni", "Plats", "Spicy salami", new BigDecimal("10.50")));
    }

    // Very small CSV parser that handles quoted fields with commas
    private String[] parseCsvLine(String line) {
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        java.util.List<String> parts = new java.util.ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
