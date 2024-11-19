package fr.unice.polytech.steats.order.singles;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.GroupOrderServiceHelper;
import fr.unice.polytech.steats.helpers.MenuItemServiceHelper;
import fr.unice.polytech.steats.helpers.PaymentServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.GroupOrder;
import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.Order;
import fr.unice.polytech.steats.models.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private final List<String> items = new ArrayList<>();
    private final String addressId;
    private final String restaurantId;
    private Status status = Status.INITIALISED;
    private final List<String> appliedDiscounts = new ArrayList<>();

    /**
     * @param userId       The user that initialized the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    public SingleOrder(String userId, LocalDateTime deliveryTime, String addressId, String restaurantId) {
        this(userId, null, deliveryTime, addressId, restaurantId);
    }

    /**
     * @param userId       The user that initialized the order
     * @param groupCode    The group code of the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    public SingleOrder(@JsonProperty("userId") String userId, @JsonProperty("groupCode") String groupCode, @JsonProperty("deliveryTime") LocalDateTime deliveryTime,
                @JsonProperty("addressId") String addressId, @JsonProperty("restaurantId") String restaurantId) {
        this.id = UUID.randomUUID().toString();
        this.orderTime = LocalDateTime.now();
        this.userId = userId;
        this.groupCode = groupCode;
        this.deliveryTime = deliveryTime;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
        // TODO: let the creation of the order to the restaurant
        //if (!RestaurantServiceHelper.canHandle(id, deliveryTime))
        //    throw new IllegalArgumentException("The restaurant can't handle the order at this delivery time");
        //SingleOrderManager.getInstance().add(this);
        //if (groupCode == null) RestaurantServiceHelper.addOrder(id);
    }

    /**
     * @param groupCode The group code of the order
     * @param userId    The user that initialized the order
     */
    public SingleOrder(@JsonProperty("groupCode") String groupCode, @JsonProperty("userId") String userId) throws IOException {
        GroupOrder groupOrder= GroupOrderServiceHelper.getGroupOrder(groupCode);
        this.id = UUID.randomUUID().toString();
        this.orderTime = LocalDateTime.now();
        this.userId = userId;
        this.groupCode = groupCode;
        this.deliveryTime = groupOrder.deliveryTime();
        this.addressId = groupOrder.addressId();
        this.restaurantId = groupOrder.restaurantId();
    }

    public boolean checkGroupOrder() throws IOException {
        if (groupCode == null) return true;
        GroupOrder groupOrder = GroupOrderServiceHelper.getGroupOrder(groupCode);
        return groupOrder.deliveryTime() == deliveryTime
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

    //TODO : Menu Item
//    /**
//     * The price without discounts
//     *
//     * @implNote Returns the sum of the price of all the {@link fr.unice.polytech.steats.restaurant.MenuItem MenuItem} it contains.
//     */
//    public double getSubPrice() {
//        return items.stream().map(item -> {
//            try {
//                return MenuItemServiceHelper.getMenuItem(item);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).mapToDouble(MenuItem::getPrice).sum();
//    }

    @Override
    public double getPrice() {
        //Todo : Discount
        /*List<Discount> oldDiscountsToApplied;
        try {
            oldDiscountsToApplied = DiscountServiceHelper.getDiscountToApplyNext(userId, restaurantId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Stream.concat(
                appliedDiscounts.stream().map(discount -> {
                    try {
                        return DiscountServiceHelper.getDiscount(discount);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }),
                oldDiscountsToApplied.stream()
        ).reduce(getSubPrice(), (price, discount) -> discount.getNewPrice(price), Double::sum);*/
        return 0;
    }

    @Override
    public List<String> getItems() {
        //TODO : Menu Item
        /*List<String> res = new ArrayList<>(items);
        res.addAll(appliedDiscounts.stream().map(item -> {
            try {
                return DiscountServiceHelper.getDiscount(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).filter(Discount::canBeAppliedDirectly).map(Discount::freeItems).flatMap(List::stream).map(MenuItem::getId).toList());
        return res;*/
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
     * @param item The id of the menu item the user chose to add to the order
     */
    public void addMenuItem(String item) throws IOException {
        items.add(item);
        //updateDiscounts();
    }

    /**
     * Remove a menu item from the items of the order
     *
     * @param item The id of the menu item the user chose to remove from the order
     */
    public void removeMenuItem(String item) throws IOException {
        items.remove(item);
        //updateDiscounts();
    }

    //TODO : Discount

//    private void updateDiscounts() throws IOException {
//        appliedDiscounts.clear();
//        appliedDiscounts.addAll(RestaurantServiceHelper.getRestaurant(restaurantId).availableDiscounts(this).stream().map(Discount::getId).toList());
//    }

//    /**
//     * Get the discounts to apply to the next order
//     */
//    public List<Discount> getDiscountsToApplyNext() {
//        return appliedDiscounts.stream().map(discount -> {
//            try {
//                return DiscountServiceHelper.getDiscount(discount);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).filter(discount -> !discount.canBeAppliedDirectly() && !discount.isExpired()).toList();
//    }

    /**
     * Get the list of discounts id triggered by the order
     */
    public List<String> getDiscounts() {
        return appliedDiscounts;
    }

    /**
     * @implNote The total preparation time of all the items in the order
     */
    @Override
    public Duration getPreparationTime() {
        return items.stream().map(addMenuItem -> {
                    try {
                        return MenuItemServiceHelper.getMenuItem(addMenuItem);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).map(MenuItem::preparationTime).reduce(Duration.ZERO, Duration::plus);
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
