package fr.unice.polytech.steats.models;

import fr.unice.polytech.steats.order.Status;

import java.time.LocalDateTime;
import java.util.List;

public record Order(
        String id,
        String userId,
        String groupCode,
        LocalDateTime deliveryTime,
        String addressId,
        String restaurantId,
        Status status,
        List<String> items,
        List<String> appliedDiscounts,
        LocalDateTime orderTime,
        double price
) {
}
