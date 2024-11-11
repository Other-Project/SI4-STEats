package fr.unice.polytech.steats.models;

import java.time.LocalDateTime;

/**
 * Represents the address where an order can be delivered
 *
 * @param id      The id of the payment
 * @param date    The date of the payment
 * @param userId  The id of the user who made the payment
 * @param orderId The id of the order that the payment is for
 * @param amount  The amount of the payment
 * @author Team C
 */
public record Payment(String id, LocalDateTime date, String userId, String orderId, Double amount) {

}
