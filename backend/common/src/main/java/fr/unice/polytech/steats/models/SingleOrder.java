package fr.unice.polytech.steats.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record SingleOrder(
        String id,
        String userId,
        String groupCode,
        LocalDateTime deliveryTime,
        String addressId,
        String restaurantId,
        Status status,
        List<String> items,
        List<String> discounts,
        Duration preparationTime,
        LocalDateTime orderTime,
        double subPrice,
        double price
) implements IOrder {
}
