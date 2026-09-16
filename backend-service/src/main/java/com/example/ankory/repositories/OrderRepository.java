package com.example.ankory.repositories;

import com.example.ankory.entities.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<FoodOrder, Long> {

    List<FoodOrder> findAllByOrderByCreatedAtDesc();

    List<FoodOrder> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
}
