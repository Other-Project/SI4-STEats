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
    private double totalPrice;
    private User user;

    /**
     * @param user the person using the application (browsing or/and ordering)
     */
    public STEats(User user) {
        this.user = user;
    }

    /**
     * @implNote The constructor for a unregistered user
     */
    public STEats(){}

    private void updateFullMenu(Order order){
        this.fullMenu = order.getRestaurant().getFullMenu();
    }

    /**
     * Create a single order.
     * @param userId The user id of the user that initialized the order
     * @param deliveryTime The time the user wants the order to be delivered
     * @param address The address the user wants the order to be delivered
     */
    public void createOrder(String userId, LocalDateTime deliveryTime, Address address, Restaurant restaurant) throws IllegalStateException {
        if (order == null) throw new IllegalStateException();
        order = new SingleOrder(userId, deliveryTime, address, restaurant);
        updateFullMenu(order);
    }

    /**
     * Create a group order.
     * @param userID The user id of the user that initialized the order
     * @param groupCode The invitation code for the group order
     * @param deliveryTime The time the group order must be delivered
     * @param address The address where the group order must be delivered
     */
    public void createGroupOrder(String userID, String groupCode, LocalDateTime deliveryTime, Address address, Restaurant restaurant) throws IllegalStateException {
        if (groupOrder == null || order == null) throw new IllegalStateException();
        groupOrder = new GroupOrder(groupCode, deliveryTime, address, restaurant);
        order = groupOrder.createOrder(userID);
        updateFullMenu(order);
    }

    /**
     * Get all the menu items available at the time of the delivery.
     */
    public List<MenuItem> getAvailableMenu() {
        return order.getRestaurant().getAvailableMenu(order.getDeliveryTime());
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
        totalPrice += menuItem.getPrice();
    }

    /**
     * Remove a menu item from the order.
     * @param menuItem The menu item to remove from the order
     */
    public void removeMenuItem(MenuItem menuItem) {
        order.removeMenuItem(menuItem);
        totalPrice -= menuItem.getPrice();
    }

    /**
     * Get the total price of the order.
     */
    public double getTotalPrice() {
        return totalPrice;
    }
}
