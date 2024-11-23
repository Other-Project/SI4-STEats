package fr.unice.polytech.steats.models;

public record Effects(double orderDiscount, double orderCredit, String... freeItemIds) {
}
