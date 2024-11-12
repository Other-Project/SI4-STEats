package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.location.Address;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.users.User;

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
     * Set the status of the order
     *
     * @param status The new status of the order
     */
    void setStatus(Status status);

    /**
     * @return The time the user wants the order to be delivered
     */
    LocalDateTime getDeliveryTime();

    /**
     * @return The address where the order must be delivered
     */
    Address getAddress();

    /**
     * The id of the restaurant in which the order is made
     */
    String getRestaurantId();

    /**
     * @return The restaurant in which the order is made
     */
    Restaurant getRestaurant();

    /**
     * Get the group code of the order
     *
     * @return null if not applicable
     */
    String getGroupCode();

    /**
     * @return A copy of the items of the order
     */
    List<MenuItem> getItems();

    /**
     * @return The menu that can be ordered at the given time
     */
    List<MenuItem> getAvailableMenu();

    /**
     * @return The List of users that have ordered
     */
    List<User> getUsers();

    /**
     * @return The time it takes to prepare the order
     */
    Duration getPreparationTime();

    /**
     * @return The ordering time of the order
     */
    LocalDateTime getOrderTime();

    /**
     * Set the delivery time of the order
     *
     * @param deliveryTime The time the order must be delivered
     */
    void setDeliveryTime(LocalDateTime deliveryTime);
}
