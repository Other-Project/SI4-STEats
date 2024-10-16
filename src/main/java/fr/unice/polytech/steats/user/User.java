package fr.unice.polytech.steats.user;

import fr.unice.polytech.steats.PaymentSystem;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The person that is currently using the system
 *
 * @author Team C
 */
public class User {
    private final String name;
    private final String userId;
    private final Role role;
    private final List<SingleOrder> ordersHistory = new ArrayList<>();

    /**
     * @param name   The name of the user
     * @param userId The id of the user
     * @param role   The role of the user
     */
    public User(String name, String userId, Role role) {
        this.name = name;
        this.userId = userId;
        this.role = role;
    }

    /**
     * Add the order to the history of the user once it has been paid
     *
     * @param order The order that has been delivered to the user
     */
    public void addOrderToHistory(SingleOrder order) {
        ordersHistory.add(order);
    }

    /**
     * @return The name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * @return The role of the user
     */
    public Role getRole() {
        return role;
    }

    /**
     * @return The user's ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Pay the totalPrice
     *
     * @param totalPrice The total price of the order
     * @return If the payment was successful
     */
    public boolean pay(double totalPrice) {
        return PaymentSystem.pay(totalPrice);
    }

    /**
     * Gets all the orders of the user
     */
    public List<Order> getOrders() {
        return Collections.unmodifiableList(ordersHistory);
    }

    /**
     * Get the discounts to apply to the next order
     */
    public List<Discount> getDiscountsToApplyNext() {
        return ordersHistory.isEmpty() ? Collections.emptyList() : ordersHistory.getLast().getDiscountsToApplyNext();
    }
}
