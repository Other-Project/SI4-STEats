package fr.unice.polytech.steats;

import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.GroupOrder;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;

import java.time.LocalDateTime;
import java.util.List;

/**
 * It is the entry point of the application.
 *
 * @author Team C
 */
public class STEats {

    private GroupOrder groupOrder;
    private SingleOrder order;
    private List<MenuItem> fullMenu;
    private List<MenuItem> availableMenu;
    private double totalPrice;

    /**
     * @param groupOrder    The group order if the user is in a group order or null if the user is not in a group order
     * @param order         The single order of the user
     * @param fullMenu      The full menu of the restaurant
     * @param availableMenu The menu available at the time of the delivery
     * @param totalPrice    The total price of the order
     */
    public STEats(GroupOrder groupOrder, SingleOrder order, List<MenuItem> fullMenu, List<MenuItem> availableMenu,double totalPrice) {
        this.groupOrder = groupOrder;
        this.order = order;
        this.fullMenu = fullMenu;
        this.availableMenu = availableMenu;
        this.totalPrice = totalPrice;
    }

    /**
     * Create a single order.
     * @param userId The user id of the user that initialized the order
     * @param deliveryTime The time the user wants the order to be delivered
     * @param address The address the user wants the order to be delivered
     */
    public void createOrder(String userId, LocalDateTime deliveryTime, Address address) {
        order = new SingleOrder(userId, deliveryTime, address);
    }

    /**
     * Create a group order.
     * @param userID The user id of the user that initialized the order
     * @param groupCode The invitation code for the group order
     * @param deliveryTime The time the group order must be delivered
     * @param address The address where the group order must be delivered
     */
    public void createGroupOrder(String userID, String groupCode, LocalDateTime deliveryTime, Address address) {
        groupOrder =  new GroupOrder(groupCode, deliveryTime, address);
        groupOrder.createOrder(userID);
    }

    /**
     * Get all the menu items available at the time of the delivery.
     * @param deliveryTime The time the user wants the order to be delivered
     * @param restaurant The restaurant where the user wants to order
     */
    public void getAvailableMenu(LocalDateTime deliveryTime, Restaurant restaurant) {
        availableMenu = restaurant.getAvailableMenu(deliveryTime);
    }

    /**
     * Get the full menu of the restaurant, including the menu items that are not available at the time of the delivery.
     * @param restaurant The restaurant where the user wants to order
     */
    public void getFullMenu(Restaurant restaurant) {
        fullMenu = restaurant.getFullMenu();
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
    public void getTotalPrice() {
        totalPrice = order.getPrice();
    }




}
