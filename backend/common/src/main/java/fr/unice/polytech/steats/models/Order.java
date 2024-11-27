package fr.unice.polytech.steats.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record Order(
        String id,
        String groupCode,
        LocalDateTime deliveryTime,
        String addressId,
        String restaurantId,
        Status status,
        Map<String, Integer> items,
        List<String> discounts,
        Duration preparationTime,
        LocalDateTime orderTime,
        double price
) implements IOrder {
}
