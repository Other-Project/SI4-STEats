package fr.unice.polytech.steats.payment;

import java.time.LocalDateTime;

/**
 * Represents the address where an order can be delivered
 *
 * @param date   The date of the payment
 * @param amount The amount of the payment
 * @author Team C
 */
public record Payment(LocalDateTime date, Double amount) {

}
