package fr.unice.polytech.steats.payments;

import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.time.LocalDateTime;
import java.util.List;

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
     * Get the payments for an order.
     *
     * @param orderId The order id
     */
    public List<Payment> getPaymentsForOrder(String orderId) {
        return getAll().stream().filter(payment -> payment.orderId().equals(orderId)).toList();
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        String johnDoe = "123456";
        String janeDoe = "654321";
        String albanFalcoz = "140403";
        add(new Payment("1", LocalDateTime.of(2023, 10, 5, 18, 20), johnDoe, "1", 10.00));
        add(new Payment("2", LocalDateTime.of(2024, 11, 8, 10, 35), johnDoe, "2", 16.50));
        add(new Payment("3", LocalDateTime.of(2024, 11, 3, 20, 4), janeDoe, "3", 3.25));
        add(new Payment("4", LocalDateTime.of(2024, 12, 1, 14, 15), johnDoe, "4", 25.00));
        add(new Payment("5", LocalDateTime.of(2024, 12, 20, 9, 45), albanFalcoz, "5", 50.75));
        add(new Payment("6", LocalDateTime.of(2025, 1, 3, 16, 30), janeDoe, "6", 100.00));
    }
}
