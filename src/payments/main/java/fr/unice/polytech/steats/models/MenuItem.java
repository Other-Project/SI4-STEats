package fr.unice.polytech.steats.models;


import java.time.Duration;

public record MenuItem(String name, String restaurantId, double price, Duration preparationTime) {
}
