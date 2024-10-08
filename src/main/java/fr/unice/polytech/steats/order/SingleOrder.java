package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single order taken by a client.
 *
 * @author Team C
 */
public class SingleOrder implements Order {
    private final User user;
    private final LocalDateTime deliveryTime;
    private final List<MenuItem> items = new ArrayList<>();
    private final Address address;
    private Status status = Status.INITIALISED;
    private Restaurant restaurant;

    /**
     *
     * @param user The user that initialized the order
     * @param deliveryTime The time the client wants the order to be delivered
     * @param address The address the client wants the order to be delivered
     * @param restaurant The restaurant in which the order is made
     */
    public SingleOrder(User user, LocalDateTime deliveryTime, Address address, Restaurant restaurant) {
        this.user = user;
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
     * @return The user that initialized the order
     */
    public User getUser() {
        return user;
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
