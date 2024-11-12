package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Will manage single orders
 * It will be able to create, delete, update, get and store single orders
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class SingleOrderManager extends AbstractManager<SingleOrder> {
    private static final SingleOrderManager INSTANCE = new SingleOrderManager();

    private SingleOrderManager() {
        super();
    }

    /**
     * Get the instance of the SingleOrderManager
     *
     * @return The instance of the SingleOrderManager
     */
    public static SingleOrderManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(SingleOrder item) {
        super.add(item.getId(), item);
    }

    /**
     * Get all the orders of a user
     *
     * @param userId The id of the user
     */
    public List<SingleOrder> getOrdersByUser(String userId) {
        return getAll().stream().filter(singleOrder -> userId.equals(singleOrder.getUserId())).toList();
    }

    /**
     * Get all the orders of a group
     *
     * @param groupCode The invitation code of the group
     */
    public List<SingleOrder> getOrdersByGroup(String groupCode) {
        return getAll().stream().filter(singleOrder -> groupCode.equals(singleOrder.getGroupCode())).toList();
    }

    /**
     * Get all the orders of a restaurant
     *
     * @param restaurantId The id of the restaurant
     */
    public List<SingleOrder> getOrdersByRestaurant(String restaurantId) {
        return getAll().stream().filter(singleOrder -> restaurantId.equals(singleOrder.getRestaurantId())).toList();
    }

    /**
     * Get all the orders of a user in a restaurant
     *
     * @param userId       The id of the user
     * @param restaurantId The id of the restaurant
     */
    public List<SingleOrder> getOrdersByUserInRestaurant(String userId, String restaurantId) {
        return getAll().stream().filter(singleOrder -> userId.equals(singleOrder.getUserId()) && restaurantId.equals(singleOrder.getRestaurantId())).toList();
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        String johnDoe = "123456";
        String janeDoe = "654321";
        String albanFalcoz = "140403";
        AddressManager.getInstance().demo();
        RestaurantManager.getInstance().demo();
        add(new SingleOrder(albanFalcoz, LocalDateTime.of(2025, 10, 5, 18, 20), "EURECOM", "1"));
        add(new SingleOrder(janeDoe, LocalDateTime.of(2025, 11, 8, 10, 35), "Campus Sophia Tech", "2"));
        add(new SingleOrder(albanFalcoz, LocalDateTime.of(2025, 10, 5, 18, 20), "Campus Sophia Tech", "1"));
        add(new SingleOrder(johnDoe, LocalDateTime.of(2025, 11, 8, 10, 35), "EURECOM", "2"));
    }
}
