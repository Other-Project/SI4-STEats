package fr.unice.polytech.steats.order;

import java.io.IOException;
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
     * The id of the restaurant in which the order is made
     */
    String getRestaurantId();

    /**
     * Get the group code of the order
     *
     * @return null if not applicable
     */
    String getGroupCode();

    /**
     * @return The time it takes to prepare the order
     */
    Duration getPreparationTime();

    /**
     * @return The ordering time of the order
     */
    LocalDateTime getOrderTime();

    /**
     * get the list of the items id's in the order
     */
    List<String> getItems();

    /**
     * get the list of the available items id's in the restaurant
     */
    List<String> getAvailableMenu() throws IOException;

    /**
     * get the list of the users id's in the order
     */
    List<String> getUsers();

    /**
     * Set the delivery time of the order
     *
     * @param deliveryTime The time the order must be delivered
     */
    void setDeliveryTime(LocalDateTime deliveryTime) throws IOException;
}
