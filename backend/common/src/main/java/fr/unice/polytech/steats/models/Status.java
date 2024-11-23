package fr.unice.polytech.steats.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The different states an order goes through between the moment the user chooses to pay and the moment the order is delivered
 *
 * @author Team C
 */
public enum Status {
    /**
     * The order has been created
     */
    @JsonProperty("INITIALISED")
    INITIALISED,
    /**
     * The client has paid his order
     */
    @JsonProperty("PAID")
    PAID,
    /**
     * The order is being prepared by the restaurant staff
     */
    @JsonProperty("IN_PREPARATION")
    IN_PREPARATION,
    /**
     * The order has been prepared and will soon be delivered
     */
    @JsonProperty("READY_FOR_DELIVERY")
    READY_FOR_DELIVERY,
    /**
     * The order is being delivered by the delivery person
     */
    @JsonProperty("IN_DELIVERY")
    IN_DELIVERY,
    /**
     * The order has been received by the client
     */
    @JsonProperty("DELIVERED")
    DELIVERED,
}
