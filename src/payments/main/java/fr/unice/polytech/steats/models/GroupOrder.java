package fr.unice.polytech.steats.models;

import fr.unice.polytech.steats.utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record GroupOrder(
        String id,
        String groupCode,
        LocalDateTime deliveryTime,
        String addressId,
        String restaurantId,
        Status status,
        List<String> ordersId,
        List<String> items,
        List<String> discounts,
        Duration preparationTime,
        LocalDateTime orderTime,
        double price
) implements IOrder {
}
