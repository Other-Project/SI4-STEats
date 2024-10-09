package fr.unice.polytech.steats.user;

import fr.unice.polytech.steats.order.Order;

import java.util.ArrayList;
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
    private final List<Order> ordersHistory = new ArrayList<>();

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
    public void addOrderToHistory(Order order) {
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

    // TODO : implement this method
    public void pay(double totalPrice) {
        System.out.println("Vous devez payer : " + totalPrice + "€");
    }
}
