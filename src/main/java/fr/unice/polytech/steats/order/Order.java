package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an order taken by a registered user
 *
 * @author Team C
 */
public interface Order extends Saleable {
    /**
     * @return The status of the order
     */
    Status getStatus();

    /**
     * @return The time the user wants the order to be delivered
     */
    LocalDateTime getDeliveryTime();

    /**
     * @return The address where the order must be delivered
     */
    Address getAddress();

    /**
     * @return A copy of the items of the order
     */
    List<MenuItem> getItems();
}
