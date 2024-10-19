package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.AbstractManager;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Will manage restaurants
 * It will be able to create, delete, update, get and store restaurants
 *
 * @author Team C
 */
public class RestaurantManager extends AbstractManager<Restaurant> {
    private static final RestaurantManager INSTANCE = new RestaurantManager();

    private RestaurantManager() {
        super();
    }

    /**
     * Get the instance of the RestaurantManager
     *
     * @return The instance of the RestaurantManager
     */
    public static RestaurantManager getInstance() {
        return INSTANCE;
    }

    /**
     * Filter the restaurants by name
     *
     * @param name The name of the restaurant
     * @return A list of restaurants that have the name given in args
     */
    public static List<Restaurant> filterRestaurantByName(String name) {
        return getInstance().items
                .values()
                .stream()
                .filter(restaurant -> restaurant.getName()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .toList();
    }

    /**
     * Filter the restaurants by type of food
     *
     * @param typeOfFood The type of food the restaurant serves
     * @return A list of restaurants that serve the type of food given in args
     */
    public static List<Restaurant> filterRestaurantByTypeOfFood(TypeOfFood typeOfFood) {
        return getInstance().items
                .values()
                .stream()
                .filter(restaurant -> restaurant.getTypeOfFood() == typeOfFood)
                .toList();
    }

    /**
     * Filter the restaurants by delivery time
     *
     * @param deliveryTime The time the user wants the order to be delivered
     * @return A list of restaurants that can deliver at the time given in args
     */
    public static List<Restaurant> filterRestaurantByDeliveryTime(LocalDateTime deliveryTime) {
        return getInstance().items
                .values()
                .stream()
                .filter(restaurant -> restaurant.canDeliverAt(deliveryTime))
                .toList();
    }
}

