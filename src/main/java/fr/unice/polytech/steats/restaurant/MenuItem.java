package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.Saleable;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.time.Duration;
import java.util.UUID;

public class MenuItem implements Saleable {
    private final String name;
    private final double price;
    private final Duration preparationTime;
    private String restaurantId;
    private String id;

    /**
     * Create a menu item
     *
     * @param name            The name of the menu item
     * @param price           The price of the menu item
     * @param preparationTime The time needed to prepare the menu item
     */
    public MenuItem(String name, double price, Duration preparationTime) {
        this.name = name;
        this.price = price;
        this.preparationTime = preparationTime;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Get the name of the menu item
     */
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

    /**
     * Get the id of the menu item
     */
    public String getId() {
        return id;
    }

    /**
     * Get the preparation time of the menu item
     */
    public Duration getPreparationTime() {
        return this.preparationTime;
    }

    /**
     * Links the menu item with a restaurant
     *
     * @param restaurantId The name of the restaurant
     * @implNote This method is package-private because it should only be used by the Restaurant class.
     */
    void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    /**
     * Get the restaurant that serves the menu item
     */
    public Restaurant getRestaurant() throws NotFoundException {
        return RestaurantManager.getInstance().get(restaurantId);
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
