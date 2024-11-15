package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractManager;

import java.time.LocalDateTime;
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
     * Get all the orders of a restaurant
     *
     * @param restaurantId The id of the restaurant
     */
    public List<GroupOrder> getOrdersByRestaurant(String restaurantId) {
        return getAll().stream().filter(groupOrder -> restaurantId.equals(groupOrder.getRestaurantId())).toList();
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        add(new GroupOrder(LocalDateTime.of(2025, 1, 1, 12, 0), "1", "1"));
        add(new GroupOrder(LocalDateTime.of(2025, 1, 1, 15, 0), "2", "1"));
        add(new GroupOrder(LocalDateTime.of(2025, 1, 1, 20, 0), "1", "2"));
        add(new GroupOrder(LocalDateTime.of(2025, 1, 1, 8, 30), "2", "2"));
    }
}
