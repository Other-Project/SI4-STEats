package fr.unice.polytech.steats.restaurant;

import java.time.LocalTime;

public class MenuItem {
    private final String name;
    private double price;
    private LocalTime preparationTime;

    public MenuItem(String name, double price, LocalTime preparationTime) {
        this.name = name;
        this.price = price;
        this.preparationTime = preparationTime;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public LocalTime getPreparationTime() {
        return this.preparationTime;
    }
}
