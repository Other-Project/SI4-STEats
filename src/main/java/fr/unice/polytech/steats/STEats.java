package fr.unice.polytech.steats;

import fr.unice.polytech.steats.order.*;
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
    private final GroupOrderManager groupOrderManager;
    private static final String ORDER_ALREADY_IN_PROGRESS = "An order is already in progress.";

    /**
     * @param user The person using the application (browsing or/and ordering)
     * @param groupOrderManager The manager of the group orders
     */
    public STEats(User user, GroupOrderManager groupOrderManager) {
        this.user = user;
        this.groupOrderManager = groupOrderManager;
    }

    /**
     * @implNote The constructor for an unregistered user
     * @param groupOrderManager The manager of the group orders
     */
    public STEats(GroupOrderManager groupOrderManager) {
        this.groupOrderManager = groupOrderManager;
    }

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
        if (order != null) throw new IllegalStateException(ORDER_ALREADY_IN_PROGRESS);
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
        if (groupOrder != null || order != null) throw new IllegalStateException(ORDER_ALREADY_IN_PROGRESS);
        groupOrder = new GroupOrder(groupCode, deliveryTime, address, restaurant);
        order = groupOrder.createOrder(user.getUserId());
        groupOrderManager.addGroupOrder(groupOrder);
        updateFullMenu(order);
    }

    /**
     * Join a group order.
     *
     * @param groupCode The invitation code for the group order
     */
    public void joinGroupOrder(String groupCode) {
        if (groupOrder != null || order != null) throw new IllegalStateException(ORDER_ALREADY_IN_PROGRESS);
        groupOrder = GroupOrderManager.getGroupOrder(groupCode);
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
}
