package fr.unice.polytech.steats.models;

import fr.unice.polytech.steats.restaurant.TypeOfFood;

import java.time.Duration;

public record Restaurant(String id, String name, TypeOfFood typeOfFood, Duration scheduleDuration) {
}
