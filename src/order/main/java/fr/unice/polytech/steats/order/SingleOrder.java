package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.helper.DiscountServiceHelper;
import fr.unice.polytech.steats.helper.MenuItemServiceHelper;
import fr.unice.polytech.steats.helper.RestaurantServiceHelper;
import fr.unice.polytech.steats.helpers.PaymentServiceHelper;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.users.UserManager;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    public SingleOrder(String userId, LocalDateTime deliveryTime, String addressId, String restaurantId) throws IOException {
        this(userId, null, deliveryTime, addressId, restaurantId);
    }

    /**
     * @param userId       The user that initialized the order
     * @param groupCode    The group code of the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param addressId    The label of the address the client wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    SingleOrder(String userId, String groupCode, LocalDateTime deliveryTime, String addressId, String restaurantId) throws IOException {
        this.id = UUID.randomUUID().toString();
        this.orderTime = LocalDateTime.now();
        if (deliveryTime != null && orderTime.plusHours(2).isAfter(deliveryTime))
            throw new IllegalArgumentException("The time between now and the delivery date is too short");
        if (!AddressManager.getInstance().contains(addressId))
            throw new IllegalArgumentException("This address is unknown");
        if (!RestaurantManager.getInstance().contains(restaurantId))
            throw new IllegalArgumentException("This restaurant is unknown");
        this.userId = userId;
        this.groupCode = groupCode;
        this.deliveryTime = deliveryTime;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
        if (!RestaurantServiceHelper.canHandle(id, deliveryTime))
            throw new IllegalArgumentException("The restaurant can't handle the order at this delivery time");
        SingleOrderManager.getInstance().add(this);
        if (groupCode == null) RestaurantServiceHelper.addOrder(id);
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
     * @implNote Returns the sum of the price of all the {@link fr.unice.polytech.steats.restaurant.MenuItem MenuItem} it contains.
     */
    public double getSubPrice() {
        return items.stream().map(item -> {
            try {
                return MenuItemServiceHelper.getMenuItem(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).mapToDouble(MenuItem::getPrice).sum();
    }

    @Override
    public double getPrice() {
        // Should compile after the implementation of the restaurant service (that include a discount microservice)

        List<Discount> oldDiscountsToApplied;
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
        ).reduce(getSubPrice(), (price, discount) -> discount.getNewPrice(price), Double::sum);
    }

    public List<String> getItems() {
        List<String> res = new ArrayList<>(items);
        res.addAll(appliedDiscounts.stream().map(item -> {
            try {
                return DiscountServiceHelper.getDiscount(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).filter(Discount::canBeAppliedDirectly).map(Discount::freeItems).flatMap(List::stream).map(MenuItem::getId).toList());
        return res;
    }

    @Override
    public List<String> getAvailableMenu() throws IOException {
        return RestaurantServiceHelper.getRestaurant(restaurantId).getAvailableMenu(deliveryTime).stream().map(MenuItem::getId).toList();
    }

    @Override
    public List<String> getUsers() {
        return List.of(userId);
    }

    /**
     * @return The user that initialized the order
     */
    public User getUser() {
        try {
            return UserManager.getInstance().get(userId);
        } catch (NotFoundException e) {
            return null;
        }
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
        updateDiscounts();
    }

    /**
     * Remove a menu item from the items of the order
     *
     * @param item The id of the menu item the user chose to remove from the order
     */
    public void removeMenuItem(String item) throws IOException {
        items.remove(item);
        updateDiscounts();
    }

    private void updateDiscounts() throws IOException {
        appliedDiscounts.clear();
        appliedDiscounts.addAll(RestaurantServiceHelper.getRestaurant(restaurantId).availableDiscounts(this).stream().map(Discount::getId).toList());
    }

    /**
     * Get the discounts to apply to the next order
     */
    public List<Discount> getDiscountsToApplyNext() {
        return appliedDiscounts.stream().map(discount -> {
            try {
                return DiscountServiceHelper.getDiscount(discount);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).filter(discount -> !discount.canBeAppliedDirectly() && !discount.isExpired()).toList();
    }

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
        ).map(MenuItem::getPreparationTime).reduce(Duration.ZERO, Duration::plus);
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
