package fr.unice.polytech.steats.order;

public interface Saleable {
    /**
     * Returns the price of the item.
     * If the item is a {@link fr.unice.polytech.steats.restaurant.MenuItem MenuItem}, it just returns it's price.
     * If the item is a {@link SingleOrder}, it returns the sum of the price of all the {@link fr.unice.polytech.steats.restaurant.MenuItem MenuItem} it contains.
     */
    double getPrice();
}
