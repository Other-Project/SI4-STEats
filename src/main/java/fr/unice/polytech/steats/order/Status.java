package fr.unice.polytech.steats.order;

/**
 * The different states an order goes through between the moment the user chooses to pay and the moment the order is delivered
 *
 * @author Team C
 */
public enum Status {
    /**
     * The order has been created
     */
    INITIALISED,
    /**
     * The client has paid his order
     */
    PAID,
    /**
     * The order is being prepared by the restaurant staff
     */
    IN_PREPARATION,
    /**
     * The order has been prepared and will soon be delivered
     */
    READY_FOR_DELIVERY,
    /**
     * The order is being delivered by the delivery person
     */
    IN_DELIVERY,
    /**
     * The order has been received by the client
     */
    DELIVERED,
}
