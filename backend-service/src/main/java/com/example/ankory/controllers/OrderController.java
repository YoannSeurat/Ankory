package com.example.ankory.controllers;

import com.example.ankory.dto.OrderRequestDto;
import com.example.ankory.dto.OrderResponseDto;
import com.example.ankory.dto.StatusUpdateDto;
import com.example.ankory.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto req) {
        log.info("POST /orders");
        return ResponseEntity.ok(orderService.createOrder(req));
    }

    @GetMapping
    public ResponseEntity<java.util.List<OrderResponseDto>> listOrders(
            @RequestParam(value = "restaurantId", required = false) Long restaurantId) {
        log.info("GET /orders restaurantId={}", restaurantId);
        return ResponseEntity.ok(orderService.listOrders(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable("id") Long id) {
        log.info("GET /orders/{}", id);
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(@PathVariable("id") Long id, @RequestBody StatusUpdateDto dto) {
        log.info("PATCH /orders/{}/status", id);
        return ResponseEntity.ok(orderService.updateStatus(id, dto.status));
    }
}
