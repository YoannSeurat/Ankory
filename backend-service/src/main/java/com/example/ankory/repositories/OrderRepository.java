package com.example.ankory.repositories;

import com.example.ankory.entities.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<FoodOrder, Long> {
}
