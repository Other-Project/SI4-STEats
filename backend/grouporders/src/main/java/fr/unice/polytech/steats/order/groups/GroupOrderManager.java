package fr.unice.polytech.steats.order.groups;

import fr.unice.polytech.steats.helpers.AddressServiceHelper;
import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.models.Status;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            if (item.getDeliveryTime() != null && item.getDeliveryTime().isBefore(LocalDateTime.now()))
                throw new IllegalArgumentException("The delivery time is in the past");
            if (AddressServiceHelper.getAddress(item.getAddressId()) == null)
                throw new IllegalArgumentException("The address doesn't exist");
            if (RestaurantServiceHelper.getRestaurant(item.getRestaurantId()) == null)
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
        LocalDate today = LocalTime.now().isBefore(LocalTime.of(18, 0)) ? LocalDate.now() : LocalDate.now().plusDays(1);
        List.of(
                new GroupOrder("1", LocalDateTime.of(2024, 10, 5, 15, 57),
                        LocalDateTime.of(2024, 10, 5, 18, 20), "EURECOM", "1", Status.PAID),
                new GroupOrder("2", LocalDateTime.of(2024, 10, 5, 20, 56),
                        LocalDateTime.of(2024, 10, 5, 17, 32), "Campus Sophia Tech", "2", Status.PAID),
                new GroupOrder("3", LocalDateTime.of(today, LocalTime.of(21, 46)),
                        LocalDateTime.of(today, LocalTime.of(19, 10)), "INRIA", "2", Status.INITIALISED),
                new GroupOrder("4", LocalDateTime.of(today, LocalTime.of(22, 50)),
                        LocalDateTime.of(today.plusMonths(5), LocalTime.of(11, 30)), "INRIA", "1", Status.INITIALISED)
        ).forEach(groupOrder -> this.add(groupOrder.getGroupCode(), groupOrder));
    }
}
