package fr.unice.polytech.steats.models;

import java.time.Duration;

/**
 * Represents a menu item
 *
 * @param id              The id of the menu item
 * @param name            The name of the menu item
 * @param price           The price of the menu item
 * @param preparationTime The time it takes to prepare the menu item
 * @param restaurantId    The id of the restaurant that serves the menu item
 */
public record MenuItem(String id, String name, double price, Duration preparationTime, String restaurantId) {

    /**
     * @return The preparation time of the menu item
     */
    public Duration getPreparationTime() {
        return preparationTime;
    }
}
