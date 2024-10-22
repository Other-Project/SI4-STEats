package fr.unice.polytech.steats;

import fr.unice.polytech.steats.order.Payment;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Utility class for communication with the payment system.
 *
 * @author Team C
 */
public class PaymentSystem {

    private PaymentSystem() {
    }

    /**
     * Pay the order.
     *
     * @param amount The amount to pay
     * @return The payment
     */
    public static Optional<Payment> pay(double amount) {
        // Here we would call the payment system API
        return Optional.of(new Payment(LocalDateTime.now(), amount));
    }
}
