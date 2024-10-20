package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;

import java.time.Duration;
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
     * @return The restaurant in which the order is made
     */
    Restaurant getRestaurant();

    /**
     * @return A copy of the items of the order
     */
    List<MenuItem> getItems();

    /**
     * @param time The time at which the menu must be available
     * @return The menu that can be ordered at the given time
     */
    List<MenuItem> getAvailableMenu(LocalDateTime time);

    /**
     * @return The List of users that have ordered
     */
    List<User> getUsers();

    /**
     * Close the order.
     * Changes its status to {@link Status#PAID}.
     * Send the order to the restaurant.
     * Add the order to the user's history.
     */
    void closeOrder();
      
    /**
     * @return The time it takes to prepare the order
     */
    Duration getPreparationTime();
}
