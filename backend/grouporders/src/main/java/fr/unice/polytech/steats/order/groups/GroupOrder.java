package fr.unice.polytech.steats.order.groups;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.models.Status;
import fr.unice.polytech.steats.utils.Order;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final String restaurantId;
    private Status status;

    /**
     * @param groupCode    The invitation code for the group order
     * @param orderTime    The time the group order was made
     * @param deliveryTime The time the group order must be delivered
     * @param addressId    The label of the address where the group order must be delivered
     * @param restaurantId The id of the restaurant in which the group order is made
     * @param status       The status of the group order
     */
    public GroupOrder(String groupCode, LocalDateTime orderTime, LocalDateTime deliveryTime, String addressId, String restaurantId, Status status) {
        this.orderTime = orderTime;
        this.deliveryTime = deliveryTime;
        this.groupCode = groupCode;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    /**
     * @param deliveryTime The time the group order must be delivered
     * @param addressId    The label of the address where the group order must be delivered
     * @param restaurantId The id of the restaurant in which the group order is made
     */
    public GroupOrder(@JsonProperty("deliveryTime") LocalDateTime deliveryTime, @JsonProperty("addressId") String addressId, @JsonProperty("restaurantId") String restaurantId) {
        this(UUID.randomUUID().toString().substring(0, 8), LocalDateTime.now(), deliveryTime, addressId, restaurantId, Status.INITIALISED);
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
    public double getPrice() throws IOException {
        return getOrders().stream().mapToDouble(SingleOrder::price).sum();
    }

    @Override
    public Map<String, Integer> getItems() throws IOException {
        return getOrders().stream()
                .map(SingleOrder::items)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    }

    /**
     * @return The total preparation time of all the single order in the group order
     */
    @Override
    public Duration getPreparationTime() throws IOException {
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
        if (getOrders().stream().noneMatch(order -> order.items().isEmpty()) && !RestaurantServiceHelper.canHandle(restaurantId, getPreparationTime(), deliveryTime))
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
    public void closeOrder() throws IOException {
        if (getOrders().stream().anyMatch(order -> order.status() != Status.PAID))
            throw new IllegalStateException("All the orders must be paid.");
        status = Status.PAID;
    }

    /**
     * @return The list of single orders in the group order
     */
    private List<SingleOrder> getOrders() throws IOException {
        return SingleOrderServiceHelper.getOrdersInGroup(groupCode);
    }

    /**
     * Get the list of single orders id's in the group order
     */
    public List<String> getOrdersId() throws IOException {
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
            if (RestaurantServiceHelper.canHandle(restaurantId, getPreparationTime(), time)) availableTimes.add(time);
            time = time.plusMinutes(30);
        }
        return availableTimes;
    }
}
