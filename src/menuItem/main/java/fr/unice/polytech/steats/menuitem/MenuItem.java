package fr.unice.polytech.steats.menuitem;

import fr.unice.polytech.steats.order.Saleable;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.time.Duration;

public class MenuItem implements Saleable {
    private final String menuItemId;
    private final String name;
    private final double price;
    private final Duration preparationTime;
    private String restaurantId;

    /**
     * Create a menu item
     *
     * @param name            The name of the menu item
     * @param price           The price of the menu item
     * @param preparationTime The time needed to prepare the menu item
     */
    public MenuItem(String menuItemId, String name, double price, Duration preparationTime) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.preparationTime = preparationTime;
    }

    /**
     * Get the name of the menu item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the id of the restaurant associated to the menu item
     */
    public String getMenuItemId() {
        return this.menuItemId;
    }

    /**
     * @implNote Returns the price of the menu item.
     */
    @Override
    public double getPrice() {
        return this.price;
    }

    /**
     * Get the preparation time of the menu item
     */
    public Duration getPreparationTime() {
        return this.preparationTime;
    }

    /**
     * Get the restaurant that serves the menu item
     */
    public String getRestaurantId() throws NotFoundException {
        return restaurantId;
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
