package fr.unice.polytech.steats.models;

import java.time.Duration;

public record MenuItem(String id, String name, double price, Duration preparationTime, String restaurantId) {
}
