package fr.unice.polytech.steats.order;

import java.time.LocalDateTime;

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
}
