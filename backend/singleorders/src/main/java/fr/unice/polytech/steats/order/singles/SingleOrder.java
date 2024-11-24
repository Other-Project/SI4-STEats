package fr.unice.polytech.steats.order.singles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.*;
import fr.unice.polytech.steats.models.*;
import fr.unice.polytech.steats.utils.Order;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a single order taken by a client.
 *
 * @author Team C
 */
public class SingleOrder implements Order {
    private final String id;
    private final String userId;
    private final String groupCode;
    private LocalDateTime deliveryTime;
    private final LocalDateTime orderTime;
    private final Map<String, Integer> orderedItems;
    private final Map<String, Integer> items;
    private double subPrice;
    private double price;
    private final String addressId;
    private final String restaurantId;
    private Status status;

    /**
     * @param id           The id of the order
     * @param userId       The user that initialized the order
     * @param groupCode    The group code of the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param orderTime    The time the order was made
     * @param items        The items in the order (discounts included)
     * @param orderedItems The items ordered by the client
     * @param subPrice     The price without discounts
     * @param price        The price with discounts
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     * @param status       The status of the order
     */
    @JsonCreator
    public SingleOrder(@JsonProperty("id") String id, @JsonProperty("userId") String userId, @JsonProperty("userId") String groupCode,
                       @JsonProperty("deliveryTime") LocalDateTime deliveryTime, @JsonProperty("orderTime") LocalDateTime orderTime,
                       @JsonProperty("items") Map<String, Integer> items, @JsonProperty("orderedItems") Map<String, Integer> orderedItems,
                       @JsonProperty("subPrice") double subPrice, @JsonProperty("price") double price,
                       @JsonProperty("addressId") String addressId, @JsonProperty("restaurantId") String restaurantId, @JsonProperty("status") Status status) {
        this.id = id;
        this.userId = userId;
        this.groupCode = groupCode;
        this.deliveryTime = deliveryTime;
        this.orderTime = orderTime;
        this.items = new HashMap<>(items);
        this.orderedItems = new HashMap<>(orderedItems);
        this.subPrice = subPrice;
        this.price = price;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    /**
     * @param userId       The user that initialized the order
     * @param groupCode    The group code of the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    public SingleOrder(String userId, String groupCode, LocalDateTime deliveryTime, String addressId, String restaurantId) {
        this(UUID.randomUUID().toString(), userId, groupCode, deliveryTime, LocalDateTime.now(), Map.of(), Map.of(), 0, 0, addressId, restaurantId, Status.INITIALISED);
    }

    /**
     * @param groupCode The group code of the order
     * @param userId    The user that initialized the order
     */
    public SingleOrder(String userId, String groupCode) throws IOException {
        this(userId, GroupOrderServiceHelper.getGroupOrder(groupCode));
    }

    /**
     * @param userId     The user that initialized the order
     * @param groupOrder The group order
     */
    private SingleOrder(String userId, GroupOrder groupOrder) {
        this(userId, groupOrder.groupCode(), groupOrder.deliveryTime(), groupOrder.addressId(), groupOrder.restaurantId());
    }

    public boolean checkGroupOrder() throws IOException {
        if (groupCode == null) return true;
        GroupOrder groupOrder = GroupOrderServiceHelper.getGroupOrder(groupCode);
        return Objects.equals(groupOrder.deliveryTime(), deliveryTime)
                && Objects.equals(groupOrder.addressId(), addressId)
                && Objects.equals(groupOrder.restaurantId(), restaurantId);
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
     * Get the id of the order
     */
    public String getId() {
        return id;
    }

    /**
     * Get the group code of the order
     *
     * @return null if not in a group order
     */
    @Override
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * The price without discounts
     *
     * @implNote Returns the sum of the price of all the {@link MenuItem MenuItem} it contains.
     */
    public double getSubPrice() {
        return subPrice;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public Map<String, Integer> getOrderedItems() {
        return orderedItems;
    }

    @Override
    public Map<String, Integer> getItems() {
        return items;
    }

    /**
     * The user id that initialized the order
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the delivery time of the order
     * Can only be called by group orders
     *
     * @param deliveryTime The time the client wants the order to be delivered
     */
    @Override
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public void setStatus(Status status) {
        if (status.compareTo(this.status) < 0 || this.status.compareTo(Status.PAID) < 0)
            throw new IllegalArgumentException("Can't change the status");
        this.status = status;
    }

    /**
     * Add a menu item to the items of the order
     *
     * @param menuItemId The id of the menu item the user chose to add to the order
     * @param quantity   The quantity of the menu item the user chose to add to the order
     */
    public void addMenuItem(String menuItemId, int quantity) throws IOException {
        MenuItem menuItem = MenuItemServiceHelper.getMenuItem(menuItemId);
        orderedItems.putIfAbsent(menuItemId, 0);
        orderedItems.merge(menuItem.id(), quantity, Integer::sum);
        updateDiscounts();
    }

    /**
     * Remove a menu item from the items of the order
     *
     * @param menuItemId The id of the menu item the user chose to remove from the order
     */
    public void removeMenuItem(String menuItemId) throws IOException {
        if (orderedItems.merge(menuItemId, -1, Integer::sum) <= 0) orderedItems.remove(menuItemId);
        updateDiscounts();
    }

    private void updateDiscounts() throws IOException {
        List<RestaurantDiscount> discounts = getDiscounts();

        subPrice = 0;
        for (Map.Entry<String, Integer> item : items.entrySet())
            subPrice += MenuItemServiceHelper.getMenuItem(item.getKey()).price() * item.getValue();
        price = discounts.stream().reduce(getSubPrice(), (p, d) -> d.getNewPrice(p), Double::sum);

        items.clear();
        items.putAll(getOrderedItems());
        discounts.stream()
                .map(discount -> discount.effects().freeItemIds())
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(itemId -> itemId, itemId -> 1, Integer::sum))
                .forEach((key, value) -> items.merge(key, value, Integer::sum));
    }

    private List<RestaurantDiscount> getDiscounts() throws IOException {
        List<RestaurantDiscount> discounts = new ArrayList<>();
        if (status == Status.INITIALISED) {
            for (AppliedDiscount appliedDiscount : AppliedDiscountServiceHelper.getUnusedDiscountsOfUser(userId))
                discounts.add(DiscountServiceHelper.getDiscount(appliedDiscount.discountId()));
            discounts.addAll(DiscountServiceHelper.getDiscountsApplicableToOrder(id));
            return discounts;
        }

        for (AppliedDiscount appliedDiscount : AppliedDiscountServiceHelper.getDiscountsAppliedToOrder(id))
            discounts.add(DiscountServiceHelper.getDiscount(appliedDiscount.discountId()));
        return discounts;
    }

    /**
     * @implNote The total preparation time of all the items in the order
     */
    @Override
    public Duration getPreparationTime() throws IOException {
        Duration preparationTime = Duration.ZERO;
        for (Map.Entry<String, Integer> item : items.entrySet())
            preparationTime = preparationTime.plus(MenuItemServiceHelper.getMenuItem(item.getKey()).preparationTime().multipliedBy(item.getValue()));
        return preparationTime;
    }

    @Override
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * Pay the order
     *
     * @return The payment of the order
     */
    public Payment pay() throws IOException {
        if (status == Status.PAID) throw new IllegalStateException("Order already paid");
        Payment payment = PaymentServiceHelper.payForOrder(id);
        if (payment == null) throw new IllegalStateException("Payment failed");
        status = Status.PAID;
        return payment;
    }
}
