package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.Role;

public record Criteria(int ordersAmount, int currentOrderItemsAmount, int itemsAmount, Role... clientRole) {
}
