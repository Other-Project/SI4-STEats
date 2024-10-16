package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.Saleable;

import java.time.Duration;

public class MenuItem implements Saleable {
    private final String name;
    private final double price;
    private final Duration preparationTime;

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

    @Override
    public String toString() {
        return this.name + " (" + this.price + "€) [" + this.preparationTime + "]";
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MenuItem menuItem)) return false;
        return menuItem.name.equals(this.name);
    }
}
