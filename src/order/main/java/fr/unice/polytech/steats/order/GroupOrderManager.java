package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;

/**
 * Will manage group orders
 * It will be able to create, delete, update, get and store group orders
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class GroupOrderManager extends AbstractManager<GroupOrder> {
    private static final GroupOrderManager INSTANCE = new GroupOrderManager();

    private GroupOrderManager() {
        super();
    }

    /**
     * Get the instance of the GroupOrderManager
     *
     * @return The instance of the GroupOrderManager
     */
    public static GroupOrderManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(GroupOrder item) {
        super.add(item.getGroupCode(), item);
    }

    /**
     * Get all the orders of a user
     *
     * @param userId The id of the user
     */
    public List<GroupOrder> getOrdersByUser(String userId) {
        return getAll().stream().filter(groupOrder -> groupOrder.getUsers().contains(userId)).toList();
    }

    /**
     * Get all the orders of a restaurant
     *
     * @param restaurantId The id of the restaurant
     */
    public List<GroupOrder> getOrdersByRestaurant(String restaurantId) {
        return getAll().stream().filter(groupOrder -> restaurantId.equals(groupOrder.getRestaurantId())).toList();
    }

    /**
     * Get all the orders of a user in a restaurant
     *
     * @param userId       The id of the user
     * @param restaurantId The id of the restaurant
     */
    public List<GroupOrder> getOrdersByUserInRestaurant(String userId, String restaurantId) {
        return getAll().stream()
                .filter(groupOrder -> groupOrder.getUsers().contains(userId))
                .filter(groupOrder -> restaurantId.equals(groupOrder.getRestaurantId()))
                .toList();

    }
}
