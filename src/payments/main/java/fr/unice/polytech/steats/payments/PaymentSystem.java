package fr.unice.polytech.steats.payments;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
     * @param orderId The order id
     * @return The payment
     */
    public static Optional<Payment> pay(String orderId) {
        String userId = "user1";
        double amount = 10.0;
        // TODO: Retrieve the user id and the amount from the order

        // Here we would call the payment system API
        var payment = new Payment(UUID.randomUUID().toString(), LocalDateTime.now(), userId, orderId, amount);
        PaymentManager.getInstance().add(payment);
        return Optional.of(payment);
    }
}
