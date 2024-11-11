package fr.unice.polytech.steats.payments;

import fr.unice.polytech.steats.helpers.OrderServiceHelper;
import fr.unice.polytech.steats.models.Order;
import fr.unice.polytech.steats.models.Payment;

import java.io.IOException;
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
    public static Optional<Payment> pay(String orderId) throws IOException {
        Order order = OrderServiceHelper.getOrder(orderId);
        if (order == null) return Optional.empty();

        // Here we would call the payment system API
        var payment = new Payment(UUID.randomUUID().toString(), LocalDateTime.now(), order.userId(), orderId, order.amount());
        PaymentManager.getInstance().add(payment);
        return Optional.of(payment);
    }
}
