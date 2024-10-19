package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.AbstractManager;

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

    public static List<Restaurant> filterRestaurantByName(String name) {
        return getInstance().items.values()
                .stream()
                .filter(restaurant -> restaurant.getName()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .toList();
    }

    public static List<Restaurant> filterRestaurantByTypeOfFood(TypeOfFood typeOfFood) {
        return getInstance().items
                .values().stream()
                .filter(restaurant -> restaurant.getTypeOfFood() == typeOfFood)
                .toList();
    }
}

