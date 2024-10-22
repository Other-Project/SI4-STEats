package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.PaymentSystem;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
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
    private final List<MenuItem> items = new ArrayList<>();
    private final String addressId;
    private final String restaurantId;
    private Payment payment;

    private Status status = Status.INITIALISED;
    private final List<Discount> appliedDiscounts = new ArrayList<>();

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
    SingleOrder(String userId, String groupCode, LocalDateTime deliveryTime, String addressId, String restaurantId) {
        this.id = UUID.randomUUID().toString();
        this.orderTime = LocalDateTime.now();
        if (deliveryTime != null && orderTime.plusHours(2).isAfter(deliveryTime))
            throw new IllegalArgumentException("The time between now and the delivery date is too short");
        if (!AddressManager.getInstance().contains(addressId))
            throw new IllegalArgumentException("This address is unknown");
        this.userId = userId;
        this.groupCode = groupCode;
        this.deliveryTime = deliveryTime;
        this.addressId = addressId;
        this.restaurantId = restaurantId;
        SingleOrderManager.getInstance().add(getId(), this);
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
     * Get the group order
     */
    public Optional<GroupOrder> getGroupOrder() {
        if (groupCode == null) return Optional.empty();
        try {
            return Optional.ofNullable(GroupOrderManager.getInstance().get(groupCode));
        } catch (NotFoundException e) {
            throw new IllegalStateException("The group order of the order is not found.");
        }
    }

    /**
     * The price without discounts
     *
     * @implNote Returns the sum of the price of all the {@link fr.unice.polytech.steats.restaurant.MenuItem MenuItem} it contains.
     */
    public double getSubPrice() {
        return items.stream().mapToDouble(MenuItem::getPrice).sum();
    }

    @Override
    public double getPrice() {
        List<Discount> oldDiscountsToApplied;
        try {
            oldDiscountsToApplied = UserManager.getInstance().get(userId).getDiscountsToApplyNext(restaurantId);
        } catch (NotFoundException e) {
            oldDiscountsToApplied = Collections.emptyList();
        }

        return Stream.concat(
                appliedDiscounts.stream().filter(Discount::canBeAppliedDirectly),
                oldDiscountsToApplied.stream()
        ).reduce(getSubPrice(), (price, discount) -> discount.getNewPrice(price), Double::sum);
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> res = new ArrayList<>(items);
        res.addAll(appliedDiscounts.stream().filter(Discount::canBeAppliedDirectly).map(Discount::freeItems).flatMap(List::stream).toList());
        return res;
    }

    @Override
    public List<MenuItem> getAvailableMenu(LocalDateTime time) {
        return getRestaurant().getAvailableMenu(time);
    }

    @Override
    public List<User> getUsers() {
        return List.of(getUser());
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
     * Get the payment of the order
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Set the delivery time of the order
     * Can only be called by group orders
     *
     * @param deliveryTime The time the client wants the order to be delivered
     */
    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * Add a menu item to the items of the order
     *
     * @param item A menu item the user chose to order
     */
    public void addMenuItem(MenuItem item) {
        items.add(item);
        updateDiscounts();
    }

    /**
     * Remove a menu item from the items of the order
     *
     * @param item A menu item the user chose to remove from the order
     */
    public void removeMenuItem(MenuItem item) {
        items.remove(item);
        updateDiscounts();
    }

    private void updateDiscounts() {
        appliedDiscounts.clear();
        appliedDiscounts.addAll(getRestaurant().availableDiscounts(this));
    }

    @Override
    public void closeOrder() {
        validateOrder();
        getRestaurant().addOrder(this);
    }

    /**
     * Get the discounts to apply to the next order
     */
    public List<Discount> getDiscountsToApplyNext() {
        return appliedDiscounts.stream().filter(discount -> !discount.canBeAppliedDirectly() && !discount.isExpired()).toList();
    }

    /**
     * Get the discounts triggered by the order
     */
    public List<Discount> getDiscounts() {
        return Collections.unmodifiableList(appliedDiscounts);
    }

    /**
     * Validate the order
     * Changes its status to {@link Status#PAID}.
     *
     * @implNote only validate the payment, doesn't close the order
     */
    public void validateOrder() {
        status = Status.PAID;
    }

    /**
     * @implNote The total preparation time of all the items in the order
     */
    @Override
    public Duration getPreparationTime() {
        return items.stream().map(MenuItem::getPreparationTime).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * Pay the order
     *
     * @param closeOrder true if the order should be closed after the payment
     * @return true if the payment is successful, false otherwise
     */
    public boolean pay(boolean closeOrder) {
        if (status == Status.PAID) throw new IllegalStateException("Order already paid");
        Optional<Payment> payment = PaymentSystem.pay(getPrice());
        if (payment.isEmpty()) return false;
        this.payment = payment.get();
        if (closeOrder) closeOrder();
        else validateOrder();
        return true;
    }
}
