package fr.unice.polytech.steats.order.groups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.utils.Order;
import fr.unice.polytech.steats.utils.Status;

import java.io.IOException;
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
    private GroupOrder(@JsonProperty("groupCode") String groupCode, @JsonProperty("deliveryTime") LocalDateTime deliveryTime,
                       @JsonProperty("addressId") String addressId, @JsonProperty("restaurantId") String restaurantId) {
        this.orderTime = LocalDateTime.now();
        this.deliveryTime = deliveryTime;
        this.groupCode = groupCode;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
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
    public String getRestaurantId() {
        return restaurantId;
    }

    @Override
    public String getAddressId() {
        return addressId;
    }

    /**
     * @implNote Returns the sum of the price of the all the {@link SingleOrder} it contains.
     */
    @Override
    public double getPrice() {
        return getOrders().stream().mapToDouble(SingleOrder::price).sum();
    }

    @Override
    public List<String> getItems() {
        return getOrders().stream().map(SingleOrder::items).flatMap(Collection::stream).toList();
    }

    /**
     * @return The total preparation time of all the single order in the group order
     */
    @Override
    public Duration getPreparationTime() {
        return getOrders().stream().map(SingleOrder::preparationTime).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * @return The invitation code for the group order
     */
    @Override
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
    @Override
    public void setDeliveryTime(LocalDateTime deliveryTime) throws IOException {
        if (this.deliveryTime != null) throw new IllegalStateException("Delivery time already set");
        if (getOrders().stream().noneMatch(order -> order.items().isEmpty()) && !RestaurantServiceHelper.canHandle(restaurantId, deliveryTime))
            throw new IllegalStateException("Delivery time not available");
        this.deliveryTime = deliveryTime;
        for (SingleOrder order : getOrders())
            SingleOrderServiceHelper.setDeliveryTime(order.id(), String.valueOf(deliveryTime));
    }

    @Override
    public void setStatus(Status status) throws IOException {
        if (status.compareTo(this.status) < 0 || this.status.compareTo(Status.PAID) < 0)
            throw new IllegalArgumentException("Can't change the status");
        this.status = status;
        for (String order : getOrdersId()) SingleOrderServiceHelper.setStatus(order, String.valueOf(status));
    }

    /**
     * Close the group order.
     * All the single orders must be paid before the group order can be closed.
     */
    public void closeOrder() {
        if (getOrders().stream().anyMatch(order -> order.status() != Status.PAID))
            throw new IllegalStateException("All the orders must be paid.");
        status = Status.PAID;
    }

    /**
     * @return The list of single orders in the group order
     */
    private List<SingleOrder> getOrders() {
        try {
            return SingleOrderServiceHelper.getOrdersInGroup(groupCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the list of single orders id's in the group order
     */
    @JsonIgnore
    public List<String> getOrdersId() {
        return getOrders().stream().map(SingleOrder::id).toList();
    }

    /**
     * Pay the single order of a user.
     *
     * @param id The single order id of the user that wants to pay
     * @return if the payment was successful
     */
    public Payment pay(String id) throws IOException {
        if (status != Status.INITIALISED) throw new IllegalStateException("The group order has been closed.");
        return SingleOrderServiceHelper.pay(id);
    }

    /**
     * Calculate the available delivery times for the group order.
     *
     * @param from          The start of the time range
     * @param numberOfTimes The number of delivery times to calculate
     */
    public List<LocalDateTime> getAvailableDeliveryTimes(LocalDateTime from, int numberOfTimes) throws IOException {
        List<LocalDateTime> availableTimes = new ArrayList<>();
        LocalDateTime time = from;
        while (availableTimes.size() < numberOfTimes && time.isBefore(from.plusMonths(1))) {
            if (RestaurantServiceHelper.canHandle(restaurantId, time)) availableTimes.add(time);
            time = time.plusMinutes(30);
        }
        return availableTimes;
    }
}
