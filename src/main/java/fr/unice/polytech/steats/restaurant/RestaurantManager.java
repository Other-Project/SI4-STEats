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
     * Filter the restaurants by name, type of food and if they can deliver at a certain time
     *
     * @param name         The name the restaurant's name should contain
     * @param type         The type of food the restaurant should serve
     * @param deliveryTime The time when the order should be delivered
     * @return A list of restaurants that match the criteria
     */
    public static List<Restaurant> filterRestaurant(String name, TypeOfFood type, LocalDateTime deliveryTime) {
        List<Restaurant> restaurantList = getInstance().getAll();
        if (name != null && !name.isEmpty()) {
            restaurantList.removeIf(restaurant -> !restaurant.getName().toLowerCase().contains(name.toLowerCase()));
        }
        if (type != null) {
            restaurantList.removeIf(restaurant -> !(restaurant.getTypeOfFood() == type));
        }
        if (deliveryTime != null) {
            restaurantList.removeIf(restaurant -> !restaurant.canDeliverAt(deliveryTime));
        }
        return restaurantList;
    }

    /**
     * Filter the restaurants by name
     *
     * @param name The name of the restaurant
     * @return A list of restaurants that have the name given in args
     */
    public static List<Restaurant> filterRestaurantByName(String name) {
        return filterRestaurant(name, null, null);
    }

    /**
     * Filter the restaurants by type of food
     *
     * @param typeOfFood The type of food the restaurant serves
     * @return A list of restaurants that serve the type of food given in args
     */
    public static List<Restaurant> filterRestaurantByTypeOfFood(TypeOfFood typeOfFood) {
        return filterRestaurant(null, typeOfFood, null);
    }

    /**
     * Filter the restaurants by delivery time
     *
     * @param deliveryTime The time the user wants the order to be delivered
     * @return A list of restaurants that can deliver at the time given in args
     */
    public static List<Restaurant> filterRestaurantByDeliveryTime(LocalDateTime deliveryTime) {
        return filterRestaurant(null, null, deliveryTime);
    }
}

