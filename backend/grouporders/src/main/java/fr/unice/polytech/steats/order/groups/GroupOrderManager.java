package fr.unice.polytech.steats.order.groups;

import fr.unice.polytech.steats.helpers.AddressServiceHelper;
import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.io.IOException;
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
        try {
            if(AddressServiceHelper.getAddress(item.getAddressId()) == null)
                throw new IllegalArgumentException("The address doesn't exist");
            if(RestaurantServiceHelper.getRestaurant(item.getRestaurantId()) == null)
                throw new IllegalArgumentException("The restaurant doesn't exist");
        } catch (IOException e) {
            throw new IllegalStateException("Bad request" + e.getMessage());
        }
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
     * Get all the users id's of a group order
     *
     * @param groupOrderId The id of the group order
     */
    public List<String> getUsers(String groupOrderId) throws IOException {
        return SingleOrderServiceHelper.getOrdersInGroup(groupOrderId).stream().map(SingleOrder::userId).toList();
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        List.of(
                new GroupOrder(LocalDateTime.of(2025, 1, 1, 12, 0), "EURECOM", "1"),
                new GroupOrder(LocalDateTime.of(2025, 1, 1, 15, 0), "Campus Sophia Tech", "1"),
                new GroupOrder(LocalDateTime.of(2025, 1, 1, 20, 0), "INRIA", "2"),
                new GroupOrder(LocalDateTime.of(2025, 1, 1, 8, 30), "INRIA", "2")
        ).forEach(groupOrder -> this.add(groupOrder.getGroupCode(), groupOrder));
    }
}
