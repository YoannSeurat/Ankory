package com.example.ankory.services;

import com.example.ankory.dto.OrderRequestDto;
import com.example.ankory.dto.OrderResponseDto;
import com.example.ankory.entities.FoodOrder;
import com.example.ankory.entities.MenuItem;
import com.example.ankory.entities.OrderLine;
import com.example.ankory.entities.OrderStatus;
import com.example.ankory.exceptions.InvalidStatusTransitionException;
import com.example.ankory.exceptions.NotFoundException;
import com.example.ankory.repositories.MenuItemRepository;
import com.example.ankory.repositories.OrderRepository;
import com.example.ankory.repositories.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto req) {
        log.info("Create order for restaurant {}", req.restaurantId);
        if (req.restaurantId == null) throw new NotFoundException("Restaurant id required");
        var restaurant = restaurantRepository.findById(req.restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + req.restaurantId));
        if (req.lines == null || req.lines.isEmpty()) throw new IllegalArgumentException("Order must contain at least one line");

        FoodOrder order = new FoodOrder();
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setCreatedAt(Instant.now());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderLine> lines = new ArrayList<>();
        for (var l : req.lines) {
            MenuItem item = menuItemRepository.findById(l.menuItemId)
                    .orElseThrow(() -> new NotFoundException("Menu item not found: " + l.menuItemId));
            BigDecimal unit = item.getPrice();
            BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(l.quantity));
            total = total.add(lineTotal);
            OrderLine ol = new OrderLine(order, item, l.quantity, unit);
            order.addLine(ol);
        }
        order.setTotalAmount(total);
        FoodOrder saved = orderRepository.save(order);

        return toDto(saved);
    }

    @Transactional
    public OrderResponseDto updateStatus(Long orderId, String statusStr) {
        log.info("Update order {} status to {}", orderId, statusStr);
        FoodOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(statusStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown status: " + statusStr);
        }
        OrderStatus cur = order.getStatus();
        if (cur == newStatus) {
            return toDto(order);
        }
        if (!isValidTransition(cur, newStatus)) {
            throw new InvalidStatusTransitionException("Cannot transition from " + cur + " to " + newStatus);
        }
        order.setStatus(newStatus);
        FoodOrder saved = orderRepository.save(order);
        return toDto(saved);
    }

    private boolean isValidTransition(OrderStatus cur, OrderStatus next) {
        if (cur == OrderStatus.IN_PREPARATION && next == OrderStatus.EN_LIVRAISON) return true;
        if (cur == OrderStatus.EN_LIVRAISON && next == OrderStatus.LIVRE) return true;
        return false;
    }

    private OrderResponseDto toDto(FoodOrder o) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.id = o.getId();
        dto.restaurantId = o.getRestaurant().getId();
        dto.status = o.getStatus().name();
        dto.totalAmount = o.getTotalAmount();
        dto.createdAt = o.getCreatedAt();
        dto.lines = o.getLines().stream().map(l -> {
            OrderResponseDto.OrderLineResponseDto lr = new OrderResponseDto.OrderLineResponseDto();
            lr.menuItemId = l.getMenuItem().getId();
            lr.quantity = l.getQuantity();
            lr.unitPrice = l.getUnitPrice();
            return lr;
        }).collect(Collectors.toList());
        return dto;
    }
}
