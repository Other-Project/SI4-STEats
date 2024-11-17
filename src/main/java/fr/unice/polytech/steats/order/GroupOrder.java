package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A group order is an order that contains multiples single order, each from a different user.
 * Each user must process the payment step to validate their order.
 * Once all the users have paid for their order, one user must validate the group order for it to be delivered.
 *
 * @author Team C
 */
public class GroupOrder implements Order {
    private LocalDateTime deliveryTime;
    private final LocalDateTime orderTime;
    private final String groupCode;
    private final String addressId;
    private Status status = Status.INITIALISED;
    private final String restaurantId;

    /**
     * @param groupCode    The invitation code for the group order
     * @param deliveryTime The time the group order must be delivered
     * @param addressId    The label of the address where the group order must be delivered
     * @param restaurantId The id of the restaurant in which the group order is made
     */
    private GroupOrder(String groupCode, LocalDateTime deliveryTime, String addressId, String restaurantId) {
        this.orderTime = LocalDateTime.now();
        if (deliveryTime != null && LocalDateTime.now().plusHours(2).isAfter(deliveryTime))
            throw new IllegalArgumentException("The time between now and the delivery date is too short");
        if (!AddressManager.getInstance().contains(addressId))
            throw new IllegalArgumentException("This address is unknown");
        if (!RestaurantManager.getInstance().contains(restaurantId))
            throw new IllegalArgumentException("This restaurant is unknown");
        this.deliveryTime = deliveryTime;
        this.groupCode = groupCode;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
        getRestaurant().addOrder(this);
    }

    /**
     * @param deliveryTime The time the group order must be delivered
     * @param addressId    The label of the address where the group order must be delivered
     * @param restaurantId The id of the restaurant in which the group order is made
     */
    public GroupOrder(LocalDateTime deliveryTime, String addressId, String restaurantId) {
        this(UUID.randomUUID().toString().substring(0, 8), deliveryTime, addressId, restaurantId);
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
        try {
            return AddressManager.getInstance().get(addressId);
        } catch (NotFoundException e) {
            throw new IllegalStateException("The address of the order is not found.");
        }
    }

    @Override
    public String getRestaurantId() {
        return restaurantId;
    }

    @Override
    public Restaurant getRestaurant() {
        try {
            return RestaurantManager.getInstance().get(restaurantId);
        } catch (NotFoundException e) {
            throw new IllegalStateException("The restaurant of the order is not found.");
        }
    }

    /**
     * @implNote Returns the sum of the price of the all the {@link SingleOrder} it contains.
     */
    @Override
    public double getPrice() {
        return getOrders().stream().mapToDouble(Order::getPrice).sum();
    }

    @Override
    public List<MenuItem> getItems() {
        return getOrders().stream().map(Order::getItems).flatMap(Collection::stream).toList();
    }

    @Override
    public List<MenuItem> getAvailableMenu() {
        return getRestaurant().getAvailableMenu(deliveryTime);
    }

    @Override
    public List<User> getUsers() {
        return getOrders().stream().map(Order::getUsers).flatMap(Collection::stream).toList();
    }

    /**
     * @return The total preparation time of all the single order in the group order
     */
    @Override
    public Duration getPreparationTime() {
        return getOrders().stream().map(Order::getPreparationTime).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getOrderTime() {
        return orderTime;
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
        if (getOrders().stream().noneMatch(order -> order.getItems().isEmpty()) && !getRestaurant().canHandle(this, deliveryTime))
            throw new IllegalStateException("Delivery time not available");
        this.deliveryTime = deliveryTime;
        for (SingleOrder order : getOrders()) order.setDeliveryTime(deliveryTime);
    }

    @Override
    public void setStatus(Status status) {
        if (status.compareTo(this.status) < 0 || this.status.compareTo(Status.PAID) < 0)
            throw new IllegalArgumentException("Can't change the status");
        this.status = status;
        for (SingleOrder order : getOrders()) order.setStatus(status);
    }

    /**
     * Add a user to the group order.
     *
     * @param userId The id of the user that joined the group order
     * @return The order created with the user ID, and with the delivery time and the address of the group order.
     */
    public SingleOrder createOrder(String userId) {
        if (status != Status.INITIALISED) throw new IllegalStateException("The group order has been closed.");
        return new SingleOrder(userId, groupCode, deliveryTime, addressId, restaurantId);
    }

    /**
     * Close the group order.
     * All the single orders must be paid before the group order can be closed.
     */
    public void closeOrder() {
        if (getOrders().stream().anyMatch(order -> order.getStatus() != Status.PAID))
            throw new IllegalStateException("All the orders must be paid.");
        status = Status.PAID;
    }

    /**
     * @return The list of single orders in the group order
     */
    public List<SingleOrder> getOrders() {
        return SingleOrderManager.getInstance().getOrdersByGroup(groupCode);
    }

    /**
     * Pay the single order of a user.
     *
     * @param order The single order of the user that wants to pay
     * @return if the payment was successful
     */
    public boolean pay(SingleOrder order) {
        if (status != Status.INITIALISED) throw new IllegalStateException("The group order has been closed.");
        return order.pay();
    }

    /**
     * Calculate the available delivery times for the group order.
     *
     * @param from          The start of the time range
     * @param numberOfTimes The number of delivery times to calculate
     * @return The list of available delivery times
     */
    public List<LocalDateTime> getAvailableDeliveryTimes(LocalDateTime from, int numberOfTimes) {
        List<LocalDateTime> availableTimes = new ArrayList<>();
        LocalDateTime time = from;
        while (availableTimes.size() < numberOfTimes && time.isBefore(from.plusMonths(1))) {
            if (getRestaurant().canHandle(this, time)) availableTimes.add(time);
            time = time.plusMinutes(30);
        }
        return availableTimes;
    }
}
