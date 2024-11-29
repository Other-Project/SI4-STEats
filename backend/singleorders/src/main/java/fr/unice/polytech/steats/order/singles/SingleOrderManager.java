package fr.unice.polytech.steats.order.singles;

import fr.unice.polytech.steats.helpers.AddressServiceHelper;
import fr.unice.polytech.steats.helpers.RestaurantServiceHelper;
import fr.unice.polytech.steats.helpers.UserServiceHelper;
import fr.unice.polytech.steats.models.Status;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
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
            if (item.getDeliveryTime().isBefore(LocalDateTime.now().plusHours(2)))
                throw new IllegalArgumentException("Minimum delivery time is: " + LocalTime.now().plusHours(2));
            if (!RestaurantServiceHelper.canAddOrder(item.getRestaurantId(), item.getDeliveryTime()))
                throw new IllegalArgumentException("The restaurant can't handle the order at this delivery time");
            if (AddressServiceHelper.getAddress(item.getAddressId()) == null)
                throw new IllegalArgumentException("The address doesn't exist");
            if (UserServiceHelper.getUser(item.getUserId()) == null)
                throw new IllegalArgumentException("The user doesn't exist");
        } catch (IOException e) {
            throw new IllegalStateException("Bad request" + e.getMessage());
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
        String johnDoe = "JohnDoe";
        String janeDoe = "JaneDoe";
        String albanFalcoz = "AlbanFalcoz";

        String sophiaTech = "Campus Sophia Tech";
        String eurecom = "EURECOM";
        String inria = "INRIA";

        String[] itemsR1 = {"1", "2"};
        String[] itemsR2 = {"3"};
        Map<String, Double> prices = Map.of(itemsR1[0], 10.0, itemsR1[1], 12.50, itemsR2[0], 8.0);

        LocalDate today = LocalTime.now().isBefore(LocalTime.of(18, 0)) ? LocalDate.now() : LocalDate.now().plusDays(1);

        List.of(
                new SingleOrder("1", albanFalcoz, null,
                        LocalDateTime.of(2023, 10, 5, 18, 20), LocalDateTime.of(2023, 10, 5, 10, 20),
                        Map.of(itemsR1[1], 1), Map.of(itemsR1[1], 1),
                        prices.get(itemsR1[1]), prices.get(itemsR1[1]) * 0.9,
                        eurecom, "1", Status.DELIVERED),
                new SingleOrder("2", janeDoe, null,
                        LocalDateTime.of(2023, 11, 8, 10, 35), LocalDateTime.of(2023, 11, 7, 10, 35),
                        Map.of(itemsR2[0], 2), Map.of(itemsR2[0], 2),
                        prices.get(itemsR2[0]) * 2, prices.get(itemsR2[0]) * 2,
                        sophiaTech, "2", Status.DELIVERED),
                new SingleOrder("3", albanFalcoz, null,
                        LocalDateTime.of(2024, 5, 21, 18, 20), LocalDateTime.of(2024, 5, 21, 16, 16),
                        Map.of(itemsR1[1], 2, itemsR1[0], 1), Map.of(itemsR1[1], 2, itemsR1[0], 1),
                        prices.get(itemsR1[1]) * 2 + prices.get(itemsR1[0]), (prices.get(itemsR1[1]) * 2 + prices.get(itemsR1[0])) * 0.9,
                        sophiaTech, "1", Status.DELIVERED),
                new SingleOrder("4", johnDoe, null,
                        LocalDateTime.of(2024, 7, 8, 12, 35), LocalDateTime.of(2024, 7, 8, 10, 24),
                        Map.of(itemsR2[0], 6), Map.of(itemsR2[0], 6),
                        prices.get(itemsR2[0]) * 6, prices.get(itemsR2[0]) * 6,
                        eurecom, "2", Status.DELIVERED),

                new SingleOrder("5", albanFalcoz, "1",
                        LocalDateTime.of(2024, 10, 5, 18, 20), LocalDateTime.of(2024, 10, 5, 15, 57),
                        Map.of(itemsR1[0], 2, itemsR1[1], 3), Map.of(itemsR1[0], 2, itemsR1[1], 3),
                        prices.get(itemsR1[0]) * 2 + prices.get(itemsR1[1]) * 3, (prices.get(itemsR1[0]) * 2 + prices.get(itemsR1[1]) * 3) * 0.9 * 0.95,
                        eurecom, "1", Status.DELIVERED),
                new SingleOrder("6", janeDoe, "1",
                        LocalDateTime.of(2024, 10, 5, 18, 20), LocalDateTime.of(2024, 10, 5, 16, 6),
                        Map.of(itemsR1[1], 8, itemsR1[0], 1), Map.of(itemsR1[1], 8, itemsR1[0], 1),
                        prices.get(itemsR1[1]) * 8 + prices.get(itemsR1[0]), (prices.get(itemsR1[1]) * 8 + prices.get(itemsR1[0])) * 0.9,
                        eurecom, "1", Status.DELIVERED),

                new SingleOrder("7", johnDoe, "2",
                        LocalDateTime.of(2024, 10, 5, 20, 56), LocalDateTime.of(2024, 10, 5, 17, 32),
                        Map.of(itemsR2[0], 4), Map.of(itemsR2[0], 4),
                        prices.get(itemsR2[0]) * 4, prices.get(itemsR2[0]) * 4 - 1,
                        sophiaTech, "2", Status.DELIVERED),
                new SingleOrder("8", janeDoe, "2",
                        LocalDateTime.of(2024, 10, 5, 20, 56), LocalDateTime.of(2024, 10, 5, 18, 20),
                        Map.of(itemsR2[0], 8), Map.of(itemsR2[0], 8),
                        prices.get(itemsR2[0]) * 8, prices.get(itemsR2[0]) * 8,
                        sophiaTech, "2", Status.DELIVERED),

                new SingleOrder("9", albanFalcoz, null,
                        LocalDateTime.of(today, LocalTime.of(21, 41)), LocalDateTime.of(today, LocalTime.of(18, 39)),
                        Map.of(itemsR1[1], 9, itemsR1[0], 2), Map.of(itemsR1[1], 9, itemsR1[0], 2),
                        prices.get(itemsR1[1]) * 9 + prices.get(itemsR1[0]) * 2, (prices.get(itemsR1[1]) * 9 + prices.get(itemsR1[0]) * 2) * 0.9,
                        eurecom, "1", Status.PAID),
                new SingleOrder("10", johnDoe, "3",
                        LocalDateTime.of(today, LocalTime.of(21, 46)), LocalDateTime.of(today, LocalTime.of(19, 10)),
                        Map.of(itemsR2[0], 10), Map.of(itemsR2[0], 11),
                        prices.get(itemsR2[0]) * 10, prices.get(itemsR2[0]) * 10,
                        inria, "2", Status.INITIALISED),
                new SingleOrder("11", janeDoe, null,
                        LocalDateTime.of(today, LocalTime.of(21, 53)), LocalDateTime.of(today, LocalTime.of(19, 20)),
                        Map.of(itemsR1[0], 1), Map.of(itemsR1[0], 1),
                        prices.get(itemsR1[0]), prices.get(itemsR1[0]) * 0.9,
                        eurecom, "1", Status.PAID),
                new SingleOrder("12", janeDoe, "3",
                        LocalDateTime.of(today, LocalTime.of(21, 46)), LocalDateTime.of(today, LocalTime.of(19, 26)),
                        Map.of(itemsR2[0], 3), Map.of(itemsR2[0], 4),
                        prices.get(itemsR2[0]) * 3, prices.get(itemsR2[0]) * 3 - 0.5,
                        inria, "2", Status.INITIALISED),
                new SingleOrder("13", johnDoe, "3",
                        LocalDateTime.of(today, LocalTime.of(21, 46)), LocalDateTime.of(today, LocalTime.of(19, 32)),
                        Map.of(itemsR2[0], 4), Map.of(itemsR2[0], 4),
                        prices.get(itemsR2[0]) * 4, prices.get(itemsR2[0]) * 4,
                        inria, "2", Status.PAID)
        ).forEach(singleOrder -> add(singleOrder.getId(), singleOrder));
    }
}
