package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Status status = Status.INITIALISED;
    private Restaurant restaurant;

    /**
     *
     * @param userId The user's ID that initialized the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param address The address the client wants the order to be delivered
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
     * @implNote Returns the sum of the price of all the {@link fr.unice.polytech.steats.restaurant.MenuItem MenuItem} it contains.
     */
    @Override
    public double getPrice() {
        return items.stream().mapToDouble(MenuItem::getPrice).sum();
    }

     @Override
    public List<MenuItem> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public List<MenuItem> getAvailableMenu(LocalDateTime time) {
        return restaurant.getFullMenu();
    }

    /**
     * @return The user id of the user that initialized the order
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Add a menu item to the items of the order
     * @param item A menu item the user chose to order
     */
    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    /**
     * Remove a menu item from the items of the order
     * @param item A menu item the user chose to remove from the order
     */
    public void removeMenuItem(MenuItem item) {
        items.remove(item);
    }
}
