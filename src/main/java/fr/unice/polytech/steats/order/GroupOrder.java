package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A group order is an order that contains multiples single order, each from a different user.
 * Each user must process the payment step to validate their order.
 * Once all the users have paid for their order, one user must validate the group order for it to be delivered.
 *
 * @author Team C
 */
public class GroupOrder implements Order {
    private final LocalDateTime deliveryTime;
    private final String groupCode;
    private final List<Order> orders = new ArrayList<>();
    private final Address address;
    private Status status = Status.INITIALISED;
    private final Restaurant restaurant;

    /**
     * @param groupCode    The invitation code for the group order
     * @param deliveryTime The time the group order must be delivered
     * @param address      The address where the group order must be delivered
     * @param restaurant   The restaurant in which the group order is made
     */
    public GroupOrder(String groupCode, LocalDateTime deliveryTime, Address address, Restaurant restaurant) {
        this.deliveryTime = deliveryTime;
        this.groupCode = groupCode;
        this.address = address;
        this.restaurant = restaurant;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public Restaurant getRestaurant() {
        return restaurant;
    }

    /**
     * @implNote Returns the sum of the price of the all the {@link SingleOrder} it contains.
     */
    @Override
    public double getPrice() {
        return orders.stream().mapToDouble(Order::getPrice).sum();
    }

    @Override
    public List<MenuItem> getItems() {
        return orders.stream().map(Order::getItems).flatMap(Collection::stream).toList();
    }

    @Override
    public List<MenuItem> getAvailableMenu(LocalDateTime time) {
        return restaurant.getFullMenu();
    }

    @Override
    public List<User> getUsers() {
        return orders.stream().map(Order::getUsers).flatMap(Collection::stream).toList();
    }

    /**
     * @return The invitation code for the group order
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * @param user The user that joined the group order
     * @return The order created with the user ID, and with the delivery time and the address of the group order.
     */
    public SingleOrder createOrder(User user) {
        if (status != Status.INITIALISED) throw new IllegalStateException("The group order has been closed.");
        SingleOrder order = new SingleOrder(user, deliveryTime, address, restaurant);
        orders.add(order);
        return order;
    }

    /**
     * Close the group order.
     * No more single order can be added.
     * Changes it's status to {@link Status#PAID}.
     */
    public void closeGroupOrder() {
        status = Status.PAID;
    }

    /**
     * @return The list of single orders in the group order
     */
    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }
}
