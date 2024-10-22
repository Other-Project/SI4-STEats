package fr.unice.polytech.steats.user;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.Payment;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The person that is currently using the system
 *
 * @author Team C
 */
public class User {
    private String name;
    private String userId;
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
     * Get username
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Get user id
     *
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Update username
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Update user id
     * @param userId the new id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the user's role
     *
     */
    public Role getRole() {
        return role;
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
     * Gets all the orders of the user
     */
    public List<Order> getOrders() {
        return Collections.unmodifiableList(ordersHistory);
    }

    /**
     * Get all the payments of the user
     */
    public List<Payment> getPayments() {
        return ordersHistory.stream().map(SingleOrder::getPayment).toList();
    }

    /**
     * Gets all the orders of the user made in a specific restaurant
     *
     * @param restaurant The restaurant to filter the orders
     */
    public List<SingleOrder> getOrders(Restaurant restaurant) {
        return ordersHistory.stream().filter(order -> order.getRestaurant().equals(restaurant)).toList();
    }

    /**
     * Get the discounts to apply to the next order
     *
     * @param restaurant The restaurant where the user wants to order
     */
    public List<Discount> getDiscountsToApplyNext(Restaurant restaurant) {
        List<SingleOrder> orders = getOrders(restaurant);
        return orders.isEmpty() ? Collections.emptyList() : orders.getLast().getDiscountsToApplyNext();
    }
}
