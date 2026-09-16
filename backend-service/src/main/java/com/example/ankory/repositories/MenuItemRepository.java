package com.example.ankory.repositories;

import com.example.ankory.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);

    java.util.Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);
}
