package fr.unice.polytech.steats.utils;

import fr.unice.polytech.steats.models.Saleable;
import fr.unice.polytech.steats.models.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

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
    void setStatus(Status status) throws IOException;

    /**
     * @return The time the user wants the order to be delivered
     */
    LocalDateTime getDeliveryTime();

    /**
     * The id of the restaurant in which the order is made
     */
    String getRestaurantId();

    /**
     * Get the address id of the order
     */
    String getAddressId();

    /**
     * Get the group code of the order
     *
     * @return null if not applicable
     */
    String getGroupCode();

    /**
     * @return The time it takes to prepare the order
     */
    Duration getPreparationTime() throws IOException;

    /**
     * @return The ordering time of the order
     */
    LocalDateTime getOrderTime();

    /**
     * Get the ordered items id's and their quantity
     */
    Map<String, Integer> getItems() throws IOException;

    /**
     * Set the delivery time of the order
     *
     * @param deliveryTime The time the order must be delivered
     */
    void setDeliveryTime(LocalDateTime deliveryTime) throws IOException;
}
