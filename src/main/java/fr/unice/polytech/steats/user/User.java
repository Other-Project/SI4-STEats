package fr.unice.polytech.steats.user;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Payment;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.order.SingleOrderManager;
import fr.unice.polytech.steats.order.Status;

import java.util.Collections;
import java.util.List;

/**
 * The person that is currently using the system
 *
 * @author Team C
 */
public class User {
    private String name;
    private final String userId;
    private final Role role;

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
     * Get username
     */
    public String getName() {
        return name;
    }

    /**
     * Get user id
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Update username
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the user's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Gets all the orders of the user
     */
    public List<SingleOrder> getOrders() {
        return SingleOrderManager.getInstance().getOrdersByUser(userId);
    }

    /**
     * Gets all the orders of the user made in a specific restaurant
     *
     * @param restaurantId The id of the restaurant to filter the orders
     */
    public List<SingleOrder> getOrders(String restaurantId) {
        return getOrders().stream()
                .filter(order -> order.getRestaurantId().equals(restaurantId))
                .filter(order -> order.getStatus().compareTo(Status.PAID) >= 0)
                .toList();
    }

    /**
     * Get all the payments of the user
     */
    public List<Payment> getPayments() {
        return getOrders().stream().filter(order -> order.getStatus().compareTo(Status.PAID) >= 0).map(SingleOrder::getPayment).toList();
    }

    /**
     * Get the discounts to apply to the next order
     *
     * @param restaurantId The id of the restaurant where the user wants to order
     */
    public List<Discount> getDiscountsToApplyNext(String restaurantId) {
        List<SingleOrder> orders = getOrders(restaurantId);
        return orders.isEmpty() ? Collections.emptyList() : orders.getLast().getDiscountsToApplyNext();
    }
}
