package fr.unice.polytech.steats;

import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.helper.RestaurantServiceHelper;
import fr.unice.polytech.steats.items.MenuItemManager;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.order.GroupOrder;
import fr.unice.polytech.steats.order.GroupOrderManager;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.order.Status;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.OpeningTime;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the entry point of the application.
 * This class is responsible for managing the order of the user.
 *
 * @author Team C
 */
public class STEats {
    private String groupCode;
    private SingleOrder order;
    private List<MenuItem> fullMenu;
    private User user;
    private static final String ORDER_ALREADY_IN_PROGRESS = "An order is already in progress.";
    private static final String GROUP_ORDER_ALREADY_CLOSED = "Group order already closed";

    /**
     * @param user The person using the application (browsing or/and ordering)
     */
    public STEats(User user) {
        this.user = user;
    }

    /**
     * @implNote The constructor for an unregistered user
     */
    public STEats() {
    }

    private void updateFullMenu() throws IOException {
        this.fullMenu = RestaurantServiceHelper.getFullMenu(order.getRestaurantId());
    }

    /**
     * Create a single order.
     *
     * @param deliveryTime The time the user wants the order to be delivered
     * @param addressId The label of the address the user wants the order to be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    public void createOrder(LocalDateTime deliveryTime, String addressId, String restaurantId) throws IllegalStateException, IOException {
        if (order != null) throw new IllegalStateException(ORDER_ALREADY_IN_PROGRESS);
        order = new SingleOrder(user.getUserId(), deliveryTime, addressId, restaurantId);
        updateFullMenu();
    }

    /**
     * Create a group order.
     *
     * @param deliveryTime The time the group order must be delivered
     * @param addressId    The label of the address where the group order must be delivered
     * @param restaurantId The id of the restaurant in which the order is made
     */
    public String createGroupOrder(LocalDateTime deliveryTime, String addressId, String restaurantId) throws IllegalStateException, IOException {
        if (this.groupCode != null || order != null) throw new IllegalStateException(ORDER_ALREADY_IN_PROGRESS);
        GroupOrder groupOrder = new GroupOrder(deliveryTime, addressId, restaurantId);
        this.groupCode = groupOrder.getGroupCode();
        GroupOrderManager.getInstance().add(groupOrder);
        order = groupOrder.createOrder(user.getUserId());
        updateFullMenu();
        return groupCode;
    }

    /**
     * Get the menu item with the given ID
     *
     * @param menuItemId The ID of the menu item
     */
    public MenuItem getMenuItem(String menuItemId) {
        try {
            return MenuItemManager.getInstance().get(menuItemId);
        } catch (NotFoundException e) {
            throw new IllegalStateException("Menu item not found");
        }
    }

    /**
     * Get the order of the user
     *
     * @return the order of the user
     */
    public SingleOrder getOrder() {
        return order;
    }

    /**
     * Get the group code of the group order
     *
     * @return The group code
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * Join a group order.
     *
     * @param groupCode The invitation code for the group order
     */
    public void joinGroupOrder(String groupCode) throws NotFoundException, IOException {
        if (this.groupCode != null || order != null) throw new IllegalStateException(ORDER_ALREADY_IN_PROGRESS);
        GroupOrder groupOrder = GroupOrderManager.getInstance().get(groupCode);
        if (groupOrder.getStatus() == Status.PAID) throw new IllegalStateException(GROUP_ORDER_ALREADY_CLOSED);
        this.groupCode = groupOrder.getGroupCode();
        order = groupOrder.createOrder(user.getUserId());
        updateFullMenu();
    }

    /**
     * Get all the id's of the menu items available at the time of the delivery.
     */
    public List<String> getAvailableMenu() throws IOException {
        return order.getAvailableMenu();
    }

    /**
     * Get the full menu of the restaurant, including the menu items that are not available at the time of the delivery.
     */
    public List<MenuItem> getFullMenu() {
        return fullMenu;
    }

    /**
     * Add a menu item to the order.
     *
     * @param menuItem The ID of the menu item to add to the order
     */
    public void addMenuItem(String menuItem) throws IOException {
        if (!getAvailableMenu().contains(getMenuItem(menuItem)))
            throw new IllegalStateException("Menu item not available");
        order.addMenuItem(menuItem);
    }

    /**
     * Remove a menu item from the order.
     *
     * @param menuItem The ID of the menu item to remove from the order
     */
    public void removeMenuItem(String menuItem) throws IOException {
        order.removeMenuItem(menuItem);
    }

    /**
     * Get the total price of the order.
     */
    public double getTotalPrice() {
        return order.getPrice();
    }

    /**
     * Get all the id's of the item that the user wants to order.
     */
    public List<String> getCart() {
        return order.getItems();
    }

    /**
     * Get the user making an order.
     */
    public User getUser() {
        return user;
    }

    /**
     * Get the list of possible delivery times for the group order
     *
     * @param from          The time from which the delivery times are calculated
     * @param numberOfTimes The number of possible delivery times
     * @return The list of possible delivery times
     */
    public List<LocalDateTime> getAvailableDeliveryTimes(LocalDateTime from, int numberOfTimes) throws NotFoundException, IOException {
        return GroupOrderManager.getInstance().get(groupCode).getAvailableDeliveryTimes(from, numberOfTimes);
    }

    /**
     * Get all the available delivery addresses (e.g. "Campus SophiaTech", "Lucioles", "IUT", etc.)
     */
    public List<String> getAddresses() {
        return AddressManager.getInstance().getAll().stream().map(Address::label).toList();
    }

    /**
     * The user wants to proceed to the payment of the order
     *
     * @return If the payment was successful
     */
    public Payment payOrder() throws NotFoundException, IOException {
        if (groupCode != null)
            return GroupOrderManager.getInstance().get(groupCode).pay(order);
        if (order.getDeliveryTime() == null) throw new IllegalStateException("Please select a delivery time");
        return order.pay();
    }

    /**
     * Determine if all the orders in the group order are paid
     */
    public boolean canCloseGroupOrder() throws NotFoundException {
        GroupOrder groupOrder = GroupOrderManager.getInstance().get(groupCode);
        return !groupOrder.getStatus().equals(Status.PAID)
                && groupOrder.getOrders().stream().allMatch(order1 -> order1.getStatus() == Status.PAID);
    }

    /**
     * Close the group order
     */
    public void closeGroupOrder() throws NotFoundException {
        if (groupCode == null) throw new IllegalStateException("No group order");
        if (!canCloseGroupOrder()) throw new IllegalStateException("All orders are not payed");
        GroupOrder groupOrder = GroupOrderManager.getInstance().get(groupCode);
        if (groupOrder.getStatus() == Status.PAID) throw new IllegalStateException(GROUP_ORDER_ALREADY_CLOSED);
        if (groupOrder.getDeliveryTime() == null) throw new IllegalStateException("Please select a delivery time");
        groupOrder.closeOrder();
    }

    /**
     * Retrieve the list of all the restaurants
     */
    public List<Restaurant> getAllRestaurants() {
        return RestaurantManager.getInstance().getAll();
    }

    /**
     * Retrieve the opening times of a restaurant
     *
     * @param restaurant The restaurant
     */
    public Map<DayOfWeek, List<OpeningTime>> getOpeningTimes(Restaurant restaurant) {
        return Arrays.stream(DayOfWeek.values()).collect(Collectors.toMap(day -> day, restaurant::getOpeningTimes));
    }

    /**
     * Change the delivery time of the group order
     *
     * @param deliveryTime The new delivery time
     */
    public void changeDeliveryTime(LocalDateTime deliveryTime) throws NotFoundException, IOException {
        (groupCode == null ? order : GroupOrderManager.getInstance().get(groupCode)).setDeliveryTime(deliveryTime);
    }
}
