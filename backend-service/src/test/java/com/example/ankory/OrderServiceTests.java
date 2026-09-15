package com.example.ankory;

import com.example.ankory.dto.OrderLineRequestDto;
import com.example.ankory.dto.OrderRequestDto;
import com.example.ankory.dto.OrderResponseDto;
import com.example.ankory.entities.OrderStatus;
import com.example.ankory.services.MenuService;
import com.example.ankory.services.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OrderServiceTests {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    @Test
    public void createOrderFlow() {
        var menus = menuService.getMenuForRestaurant(1L);
        Assertions.assertFalse(menus.isEmpty(), "Seeded menu should exist");

        OrderRequestDto req = new OrderRequestDto();
        req.restaurantId = 1L;
        var line = new OrderLineRequestDto();
        line.menuItemId = menus.get(0).id;
        line.quantity = 2;
        req.lines = List.of(line);

        OrderResponseDto resp = orderService.createOrder(req);
        Assertions.assertNotNull(resp.id);
        Assertions.assertEquals(OrderStatus.IN_PREPARATION.name(), resp.status);
        Assertions.assertTrue(resp.totalAmount.doubleValue() > 0);
    }

    @Test
    public void statusTransitionFlow() {
        // create
        OrderRequestDto req = new OrderRequestDto();
        req.restaurantId = 1L;
        var menu = menuService.getMenuForRestaurant(1L).get(0);
        var line = new OrderLineRequestDto();
        line.menuItemId = menu.id;
        line.quantity = 1;
        req.lines = List.of(line);
        OrderResponseDto resp = orderService.createOrder(req);

        // IN_PREPARATION -> EN_LIVRAISON
        OrderResponseDto after = orderService.updateStatus(resp.id, OrderStatus.EN_LIVRAISON.name());
        Assertions.assertEquals(OrderStatus.EN_LIVRAISON.name(), after.status);

        // EN_LIVRAISON -> LIVRE
        OrderResponseDto done = orderService.updateStatus(resp.id, OrderStatus.LIVRE.name());
        Assertions.assertEquals(OrderStatus.LIVRE.name(), done.status);
    }

    @Test
    public void invalidTransition() {
        OrderRequestDto req = new OrderRequestDto();
        req.restaurantId = 1L;
        var menu = menuService.getMenuForRestaurant(1L).get(0);
        var line = new OrderLineRequestDto();
        line.menuItemId = menu.id;
        line.quantity = 1;
        req.lines = List.of(line);
        OrderResponseDto resp = orderService.createOrder(req);

        // Try to go directly to LIVRE
        Assertions.assertThrows(RuntimeException.class, () -> {
            orderService.updateStatus(resp.id, OrderStatus.LIVRE.name());
        });
    }
}
