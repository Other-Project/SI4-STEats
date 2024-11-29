package fr.unice.polytech.steats.order.singles;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.*;
import fr.unice.polytech.steats.models.*;
import fr.unice.polytech.steats.utils.Order;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private Duration preparationTime = Duration.ZERO;
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
     * @param orderedItems The items ordered by the client
     * @param items        The items in the order (free items from discounts included)
     * @param subPrice     The price without discounts
     * @param price        The price with discounts
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     * @param status       The status of the order
     */
    @SuppressWarnings("java:S107")
    SingleOrder(String id, String userId, String groupCode, LocalDateTime deliveryTime, LocalDateTime orderTime,
                Map<String, Integer> orderedItems, Map<String, Integer> items,
                double subPrice, double price, String addressId, String restaurantId, Status status) {
        this.id = id;
        this.userId = userId;
        this.groupCode = groupCode;
        this.deliveryTime = deliveryTime;
        this.orderTime = orderTime;
        this.orderedItems = new HashMap<>(orderedItems);
        this.items = new HashMap<>(items);
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
    private SingleOrder(String userId, String groupCode, LocalDateTime deliveryTime, String addressId, String restaurantId) {
        this(UUID.randomUUID().toString(), userId, groupCode, deliveryTime, LocalDateTime.now(),
                Map.of(), Map.of(), 0, 0, addressId, restaurantId, Status.INITIALISED);
    }

    /**
     * @param userId       The user that initialized the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    public SingleOrder(@JsonProperty("userId") String userId, @JsonProperty("deliveryTime") LocalDateTime deliveryTime,
                       @JsonProperty("addressId") String addressId, @JsonProperty("restaurantId") String restaurantId) {
        this(userId, null, deliveryTime, addressId, restaurantId);
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
        return Collections.unmodifiableMap(orderedItems);
    }

    @Override
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
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
     * Modify the quantity of a menu item in the order (or add it if it's not already in the order)
     *
     * @param menuItemId The id of the menu item to modify in the cart
     * @param quantity   The quantity of the menu item desired
     */
    public void modifyMenuItem(String menuItemId, int quantity) throws IOException {
        if (status != Status.INITIALISED)
            throw new IllegalStateException("Can't modify an order that has already been paid");
        if (quantity < 0) throw new IllegalArgumentException("Quantity must be positive");
        if (quantity == 0) orderedItems.remove(menuItemId);
        else {
            MenuItem menuItem = MenuItemServiceHelper.getMenuItem(menuItemId); // Check if the menu item exists
            long previousQuantity = orderedItems.getOrDefault(menuItemId, 0);
            if (previousQuantity == quantity) return;
            Duration newPreparationTime;
            if (previousQuantity > quantity)
                newPreparationTime = preparationTime.minus(menuItem.preparationTime().multipliedBy(previousQuantity - quantity));
            else
                newPreparationTime = preparationTime.plus(menuItem.preparationTime().multipliedBy(quantity - previousQuantity));
            if (!RestaurantServiceHelper.canHandlePreparationTime(restaurantId, newPreparationTime, deliveryTime))
                throw new IllegalArgumentException("The restaurant can't handle " + quantity + " more " + menuItem.name() + " in time");
            preparationTime = newPreparationTime;
            orderedItems.put(menuItem.id(), quantity);
        }
        updateDiscounts();
    }

    public void updateDiscounts() throws IOException {
        Pair discounts = getDiscounts();

        subPrice = 0;
        for (Map.Entry<String, Integer> item : getOrderedItems().entrySet())
            subPrice += MenuItemServiceHelper.getMenuItem(item.getKey()).price() * item.getValue();
        price = discounts.currentOrder.stream().reduce(subPrice, (p, d) -> d.getNewPrice(p), Double::sum);

        items.clear();
        items.putAll(getOrderedItems());
        discounts.currentOrder.stream()
                .map(discount -> discount.effects().freeItemIds())
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(itemId -> itemId, itemId -> 1, Integer::sum))
                .forEach((key, value) -> items.merge(key, value, Integer::sum));

        if (status != Status.INITIALISED)
            AppliedDiscountServiceHelper.unlockDiscounts(id, userId, discounts.toMap(id));
    }

    private record Pair(List<RestaurantDiscount> currentOrder, List<RestaurantDiscount> nextOrder) {
        List<Map.Entry<String, String>> toMap(String orderId) {
            return Stream.concat(
                    currentOrder.stream().map(discount -> Map.entry(discount.id(), orderId)),
                    nextOrder.stream().map(discount -> new AbstractMap.SimpleEntry<>(discount.id(), (String) null))
            ).toList();
        }
    }

    private Pair getDiscounts() throws IOException {
        Pair result = new Pair(new ArrayList<>(), new ArrayList<>());
        if (status == Status.INITIALISED) {
            for (AppliedDiscount appliedDiscount : AppliedDiscountServiceHelper.getUnusedDiscountsOfUser(userId))
                result.currentOrder.add(DiscountServiceHelper.getDiscount(appliedDiscount.discountId()));
            for (RestaurantDiscount discount : DiscountServiceHelper.getDiscountsApplicableToOrder(id)) {
                if (discount.options().appliesAfterOrder()) result.nextOrder.add(discount);
                else result.currentOrder.add(discount);
            }
            return result;
        }

        for (AppliedDiscount appliedDiscount : AppliedDiscountServiceHelper.getDiscountsAppliedToOrder(id))
            result.currentOrder.add(DiscountServiceHelper.getDiscount(appliedDiscount.discountId()));
        return result;
    }

    /**
     * @implNote The total preparation time of all the items in the order
     */
    @Override
    public Duration getPreparationTime() {
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
        updateDiscounts();
        return payment;
    }
}
