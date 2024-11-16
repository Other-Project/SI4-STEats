package fr.unice.polytech.steats.menuitem;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.utils.Saleable;

import java.time.Duration;

public class MenuItem implements Saleable {
    private final String id;
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
    public MenuItem(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("price") double price, @JsonProperty("preparationTime") Duration preparationTime, @JsonProperty("restaurantId") String restaurantId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.preparationTime = preparationTime;
        this.restaurantId = restaurantId;
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
    public String getId() {
        return this.id;
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
    public String getRestaurantId() {
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
