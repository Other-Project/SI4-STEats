package fr.unice.polytech.steats.payments;

import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;
import java.util.Optional;

/**
 * Manage payments (store payments, and allow to create, delete and get them)
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class PaymentManager extends AbstractManager<Payment> {
    private static final PaymentManager INSTANCE = new PaymentManager();

    private PaymentManager() {
        super();
    }

    /**
     * Get the instance of the {@link PaymentManager}
     */
    public static PaymentManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Payment item) {
        add(item.id(), item);
    }

    /**
     * Get the payments of a user.
     *
     * @param userId The user id
     */
    public List<Payment> getPaymentsOfUser(String userId) {
        return getAll().stream().filter(payment -> payment.userId().equals(userId)).toList();
    }

    /**
     * Get the payment for an order.
     *
     * @param orderId The order id
     * @return empty if no payment is found
     */
    public Optional<Payment> getPaymentForOrder(String orderId) {
        return getAll().stream().filter(payment -> payment.orderId().equals(orderId)).findFirst();
    }
}
