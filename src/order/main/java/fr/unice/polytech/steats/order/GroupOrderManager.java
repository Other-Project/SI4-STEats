package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;
import java.util.Objects;

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
     * Get all the orders of a restaurant
     *
     * @param restaurantId The id of the restaurant
     */
    public List<GroupOrder> getOrdersByRestaurant(String restaurantId) {
        return getAll().stream().filter(groupOrder -> restaurantId.equals(groupOrder.getRestaurantId())).toList();
    }

    /**
     * Get all the users id's in the group order
     *
     * @param groupCode The groupCode of the group order
     */
    public List<String> getUsers(String groupCode) {
        return SingleOrderManager.getInstance().getAll().stream()
                .filter(order -> Objects.equals(order.getGroupCode(), groupCode))
                .map(SingleOrder::getUserId).toList();
    }
}
