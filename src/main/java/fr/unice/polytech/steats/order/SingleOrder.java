package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a single order taken by a client.
 *
 * @author Team C
 */
public class SingleOrder implements Order {
    private final String userId;
    private final LocalDateTime deliveryTime;
    private final List<MenuItem> items = new ArrayList<>();
    private final Address address;
    private final Restaurant restaurant;

    private Status status = Status.INITIALISED;
    private final List<Discount> appliedDiscounts = new ArrayList<>();

    /**
     * @param userId         The user that initialized the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param address      The address the client wants the order to be delivered
     * @param restaurant   The restaurant in which the order is made
     */
    public SingleOrder(String userId, LocalDateTime deliveryTime, Address address, Restaurant restaurant) {
        this.userId = userId;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.restaurant = restaurant;
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
            oldDiscountsToApplied = UserManager.getInstance().get(userId).getDiscountsToApplyNext(restaurant);
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
        return restaurant.getAvailableMenu(time);
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
        appliedDiscounts.addAll(restaurant.availableDiscounts(this));
    }

    @Override
    public void closeOrder() {
        validateOrder();
        this.getUser().addOrderToHistory(this);
        restaurant.addOrder(this);
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

    /**
     * Pay the order
     */
    public boolean pay(boolean closeOrder) throws NotFoundException {
        if (status == Status.PAID) throw new IllegalStateException("Order already paid");
        User user = UserManager.getInstance().get(userId);
        if (!user.pay(getPrice())) return false;
        if (closeOrder) closeOrder();
        else validateOrder();
        return true;
    }
}
