package fr.unice.polytech.steats.models;

import java.io.IOException;

/**
 * Means that the item is saleable.
 *
 * @author Team C
 */
public interface Saleable {
    /**
     * Returns the price of the item.
     */
    double getPrice() throws IOException;
}
