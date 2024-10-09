package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.Saleable;

import java.time.Duration;

public class MenuItem implements Saleable {
    private final String name;
    private double price;
    private Duration preparationTime;

    public MenuItem(String name, double price, Duration preparationTime) {
        this.name = name;
        this.price = price;
        this.preparationTime = preparationTime;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @implNote Returns the price of the menu item.
     */
    @Override
    public double getPrice() {
        return this.price;
    }

    public Duration getPreparationTime() {
        return this.preparationTime;
    }
}
