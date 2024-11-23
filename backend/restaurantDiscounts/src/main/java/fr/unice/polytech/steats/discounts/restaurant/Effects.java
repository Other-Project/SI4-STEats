package fr.unice.polytech.steats.discounts.restaurant;

public record Effects(double orderDiscount, double orderCredit, String... freeItemIds) {
}
