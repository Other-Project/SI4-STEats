package fr.unice.polytech.steats.restaurant;

import java.sql.Time;

public class MenuItem {
    private final String name;
    private double price;
    private Time preparationTime;

    public MenuItem(String name, double price, Time preparationTime) {
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

    public Time getPreparationTime() {
        return this.preparationTime;
    }
}
