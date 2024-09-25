package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.MenuItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SingleOrder implements Order {
    private final String userId;
    private final LocalDateTime deliveryTime;
    private final List<MenuItem> items = new ArrayList<>();
    private final Address address;
    private Status status = Status.INITIALISED;

    public SingleOrder(String userId, LocalDateTime deliveryTime, Address address) {
        this.userId = userId;
        this.deliveryTime = deliveryTime;
        this.address = address;
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
    public double getPrice() {
        return items.stream().mapToDouble(MenuItem::getPrice).sum();
    }

    public String getUserId() {
        return userId;
    }

    public void addMenuItem(MenuItem item) {
        items.add(item);
    }

    public List<MenuItem> getItems() {
        return new ArrayList<>(items);
    }
}
