package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.helpers.PaymentServiceHelper;
import fr.unice.polytech.steats.items.DiscountManager;
import fr.unice.polytech.steats.items.MenuItemManager;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.users.UserManager;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private Payment payment;

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
    SingleOrder(String userId, String groupCode, LocalDateTime deliveryTime, String addressId, String restaurantId) {
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
        if (!getRestaurant().canHandle(this, deliveryTime))
            throw new IllegalArgumentException("The restaurant can't handle the order at this delivery time");
        SingleOrderManager.getInstance().add(this);
        if (groupCode == null) getRestaurant().addOrder(this);
    }

    /**
     * Get a menu item from its id
     *
     * @param id The id of the menu item
     */
    public MenuItem getMenuItem(String id) {
        try {
            return MenuItemManager.getInstance().get(id);
        } catch (NotFoundException e) {
            throw new IllegalStateException("The menu item of the order is not found.");
        }
    }

    /**
     * Get a discount from its id
     *
     * @param id The id of the discount
     */
    public Discount getDiscount(String id) {
        try {
            return DiscountManager.getInstance().get(id);
        } catch (NotFoundException e) {
            throw new IllegalStateException("The discount of the order is not found.");
        }
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
        return items.stream().map(this::getMenuItem).mapToDouble(MenuItem::getPrice).sum();
    }

    @Override
    public double getPrice() {
        // Should compile after the implementation of the restaurant service (that include a discount microservice)

//        List<Discount> oldDiscountsToApplied;
//        try {
//            oldDiscountsToApplied = UserManager.getInstance().get(userId).getDiscountsToApplyNext(restaurantId);
//        } catch (NotFoundException e) {
//            oldDiscountsToApplied = Collections.emptyList();
//        }
//
//        return Stream.concat(
//                appliedDiscounts.stream().map(this::getDiscount),
//                oldDiscountsToApplied.stream()
//        ).reduce(getSubPrice(), (price, discount) -> discount.getNewPrice(price), Double::sum);
        return 0;
    }

    @Override
    public List<MenuItem> getItems() {
        List<MenuItem> res = new ArrayList<>(items.stream().map(this::getMenuItem).toList());
        res.addAll(appliedDiscounts.stream().map(this::getDiscount).filter(Discount::canBeAppliedDirectly).map(Discount::freeItems).flatMap(List::stream).toList());
        return res;
    }

    @Override
    public List<MenuItem> getAvailableMenu() {
        return getRestaurant().getAvailableMenu(deliveryTime);
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
    public void addMenuItem(String item) {
        items.add(item);
        updateDiscounts();
    }

    /**
     * Remove a menu item from the items of the order
     *
     * @param item The id of the menu item the user chose to remove from the order
     */
    public void removeMenuItem(String item) {
        items.remove(item);
        updateDiscounts();
    }

    private void updateDiscounts() {
        // Should compile after the implementation of the restaurant service
        // appliedDiscounts.clear();
        // appliedDiscounts.addAll(getRestaurant().availableDiscounts(this));
    }

    /**
     * Get the discounts to apply to the next order
     */
    public List<Discount> getDiscountsToApplyNext() {
        return appliedDiscounts.stream().map(this::getDiscount).filter(discount -> !discount.canBeAppliedDirectly() && !discount.isExpired()).toList();
    }

    /**
     * Get the discounts triggered by the order
     */
    public List<Discount> getDiscounts() {
        return appliedDiscounts.stream().map(this::getDiscount).toList();
    }

    /**
     * @implNote The total preparation time of all the items in the order
     */
    @Override
    public Duration getPreparationTime() {
        return items.stream().map(this::getMenuItem).map(MenuItem::getPreparationTime).reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    /**
     * Pay the order
     *
     * @return true if the payment is successful, false otherwise
     */
    public boolean pay() throws IOException {
        if (status == Status.PAID) throw new IllegalStateException("Order already paid");
        this.payment = PaymentServiceHelper.payForOrder(id);
        status = Status.PAID;
        return true;
    }
}
