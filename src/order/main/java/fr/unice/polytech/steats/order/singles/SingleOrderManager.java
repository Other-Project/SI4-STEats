package fr.unice.polytech.steats.order.singles;

import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
        try {
            if (!RestaurantServiceHelper.canHandle(item.getRestaurantId(), item.getDeliveryTime()))
                throw new IllegalArgumentException("The restaurant can't handle the order at this delivery time");
        } catch (IOException e) {
            throw new IllegalStateException("This order's restaurant does not exist (order's restaurantId : " + item.getRestaurantId() + ")");
        }
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
        return getAll().stream().filter(singleOrder -> Objects.equals(groupCode, singleOrder.getGroupCode())).toList();
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
     * Get all the orders of a user with a specific group order
     *
     * @param userId    The id of the user
     * @param groupCode The groupCode
     */
    public List<SingleOrder> getOrdersByUser(String userId, String groupCode) {
        return getAll().stream().filter(singleOrder -> userId.equals(singleOrder.getUserId()) && Objects.equals(groupCode, singleOrder.getGroupCode())).toList();
    }

    /**
     * Get all the orders of a restaurant with a specific group order
     *
     * @param restaurantId The id of the restaurant
     * @param groupCode    The groupCode
     */
    public List<SingleOrder> getOrdersByRestaurant(String restaurantId, String groupCode) {
        return getAll().stream().filter(singleOrder -> Objects.equals(groupCode, singleOrder.getGroupCode()) && restaurantId.equals(singleOrder.getRestaurantId())).toList();
    }

    /**
     * Get all the order of a user in a restaurant with a specific group order
     *
     * @param userId       The id of the user
     * @param restaurantId The id of the restaurant
     * @param groupCode    The groupCode
     */
    public List<SingleOrder> getOrdersByUserInRestaurant(String userId, String restaurantId, String groupCode) {
        return getAll().stream()
                .filter(singleOrder -> userId.equals(singleOrder.getUserId()))
                .filter(singleOrder -> restaurantId.equals(singleOrder.getRestaurantId()))
                .filter(singleOrder -> Objects.equals(groupCode, singleOrder.getGroupCode()))
                .toList();
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        String johnDoe = "123456";
        String janeDoe = "654321";
        String albanFalcoz = "140403";
        add(new SingleOrder(albanFalcoz, LocalDateTime.of(2025, 10, 5, 18, 20), "EURECOM", "1"));
        add(new SingleOrder(janeDoe, LocalDateTime.of(2025, 11, 8, 10, 35), "Campus Sophia Tech", "2"));
        add(new SingleOrder(albanFalcoz, LocalDateTime.of(2025, 10, 5, 18, 20), "Campus Sophia Tech", "1"));
        add(new SingleOrder(johnDoe, LocalDateTime.of(2025, 11, 8, 10, 35), "EURECOM", "2"));
        add(new SingleOrder(albanFalcoz, "1", LocalDateTime.of(2025, 10, 5, 18, 20), "EURECOM", "1"));
    }
}
