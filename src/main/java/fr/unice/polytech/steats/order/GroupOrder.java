package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A group order is an order that contains multiples single order, each from a different user.
 * Each user must process the payment step to validate their order.
 * Once all the users have paid for their order, one user must validate the group order for it to be delivered.
 *
 * @author Team C
 */
public class GroupOrder implements Order {
    private LocalDateTime deliveryTime;
    private final String groupCode;
    private final List<SingleOrder> orders = new ArrayList<>();
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

    /**
     * @param deliveryTime The time the group order must be delivered
     * @param address      The address where the group order must be delivered
     * @param restaurant   The restaurant in which the group order is made
     */
    public GroupOrder(LocalDateTime deliveryTime, Address address, Restaurant restaurant) {
        this(UUID.randomUUID().toString().substring(0, 8), deliveryTime, address, restaurant);
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
     * @return The total preparation time of all the single order in the group order
     */
    @Override
    public Duration getPreparationTime() {
        return orders.stream().map(Order::getPreparationTime).reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * @return The invitation code for the group order
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * Change the delivery time of the group order.
     * The delivery time of the single orders will be updated as well.
     *
     * @param deliveryTime The time the group order must be delivered
     * @implNote The delivery time can only be set once.
     */
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        if (this.deliveryTime != null) throw new IllegalStateException("Delivery time already set");
        this.deliveryTime = deliveryTime;
        for (SingleOrder order : orders) {
            order.setDeliveryTime(deliveryTime);
        }
    }

    /**
     * @param user The user that joined the group order
     *
     * @return The order created with the user ID, and with the delivery time and the address of the group order.
     */
    public SingleOrder createOrder(User user) {
        if (status != Status.INITIALISED) throw new IllegalStateException("The group order has been closed.");
        SingleOrder order = new SingleOrder(user.getUserId(), deliveryTime, address, restaurant);
        orders.add(order);
        return order;
    }

    @Override
    public void closeOrder() {
        status = Status.PAID;
        orders.forEach(SingleOrder::closeOrder);
    }

    /**
     * @return The list of single orders in the group order
     */
    public List<SingleOrder> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    /**
     * @param order The single order of the user that wants to pay
     *
     * @return if the payment was successful
     */
    public boolean pay(SingleOrder order) throws NotFoundException {
        if (status != Status.INITIALISED) throw new IllegalStateException("The group order has been closed.");
        return order.pay(false);
    }
}
