package fr.unice.polytech.steats;

import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.GroupOrder;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the entry point of the application.
 * This class is responsible for managing the order of the user.
 * @author Team C
 */
public class STEats {
    private GroupOrder groupOrder;
    private SingleOrder order;
    private List<MenuItem> fullMenu;
    private User user;

    /**
     * @param user The person using the application (browsing or/and ordering)
     */
    public STEats(User user) {
        this.user = user;
    }

    /**
     * @implNote The constructor for an unregistered user
     */
    public STEats(){}

    private void updateFullMenu(Order order) {
        this.fullMenu = order.getRestaurant().getFullMenu();
    }

    /**
     * Create a single order.
     * @param deliveryTime The time the user wants the order to be delivered
     * @param address The address the user wants the order to be delivered
     * @param restaurant The restaurant in which the order is made
     */
    public void createOrder(LocalDateTime deliveryTime, Address address, Restaurant restaurant) throws IllegalStateException {
        if (order != null) throw new IllegalStateException("An order is already in progress.");
        order = new SingleOrder(user.getUserId(), deliveryTime, address, restaurant);
        updateFullMenu(order);
    }

    /**
     * Create a group order.
     * @param groupCode The invitation code for the group order
     * @param deliveryTime The time the group order must be delivered
     * @param address The address where the group order must be delivered
     * @param restaurant The restaurant in which the group order is made
     */
    public void createGroupOrder(String groupCode, LocalDateTime deliveryTime, Address address, Restaurant restaurant) throws IllegalStateException {
        if (groupOrder != null || order != null) throw new IllegalStateException("An order is already in progress.");
        groupOrder = new GroupOrder(groupCode, deliveryTime, address, restaurant);
        order = groupOrder.createOrder(user.getUserId());
        updateFullMenu(order);
    }

    /**
     * Get all the menu items available at the time of the delivery.
     */
    public List<MenuItem> getAvailableMenu() {
        return order.getAvailableMenu(order.getDeliveryTime());
    }

    /**
     * Get the full menu of the restaurant, including the menu items that are not available at the time of the delivery.
     */
    public List<MenuItem> getFullMenu() {
        return fullMenu;
    }

    /**
     * Add a menu item to the order.
     * @param menuItem The menu item to add to the order
     */
    public void addMenuItem(MenuItem menuItem) {
        order.addMenuItem(menuItem);
    }

    /**
     * Remove a menu item from the order.
     * @param menuItem The menu item to remove from the order
     */
    public void removeMenuItem(MenuItem menuItem) {
        order.removeMenuItem(menuItem);
    }

    /**
     * Get the total price of the order.
     */
    public double getTotalPrice() {
        return order.getPrice();
    }

    /**
     * Get all the item that the user wants to order.
     */
    public List<MenuItem> getCart() {
        return order.getItems();
    }

    /**
     * Get the user making an order.
     */
    public User getUser() {
        return user;
    }

    /**
     * The user wants to proceed to the payment of the order
     */
    public void payOrder() {
        user.pay(getTotalPrice());
        sendOrderToRestaurant(order.getRestaurant());
    }

    /**
     * Send an order to a restaurant
     *
     * @param restaurant the restaurant where to send the order
     */
    private void sendOrderToRestaurant(Restaurant restaurant) {
        restaurant.addOrder(order);
    }
}
