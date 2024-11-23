package fr.unice.polytech.steats.models;

public record Criteria(int ordersAmount, int currentOrderItemsAmount, int itemsAmount, Role... clientRole) {
}
