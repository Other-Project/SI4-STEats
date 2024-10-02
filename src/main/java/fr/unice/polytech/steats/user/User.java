package fr.unice.polytech.steats.user;

import fr.unice.polytech.steats.order.Order;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String name;
    private final String userId;
    private final Role role;
    private final List<Order> ordersHistory = new ArrayList<>();

    public User(String name, String userId, Role role) {
        this.name = name;
        this.userId = userId;
        this.role = role;
    }

    public void addOrderToHistory(Order order) {
        ordersHistory.add(order);
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }
}
