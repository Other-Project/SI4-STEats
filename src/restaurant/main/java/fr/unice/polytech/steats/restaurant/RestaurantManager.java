package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.utils.AbstractManager;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Will manage restaurants
 * It will be able to create, delete, update, get and store restaurants
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
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

    @Override
    public void add(Restaurant item) {
        super.add(item.getId(), item);
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
        String nameFilter = name == null || name.isEmpty() ? null : name.toLowerCase();
        return getInstance().getAll()
                .stream()
                .filter(restaurant -> nameFilter == null || restaurant.getName().toLowerCase().contains(nameFilter))
                .filter(restaurant -> type == null || restaurant.getTypeOfFood() == type)
                .filter(restaurant -> deliveryTime == null || restaurant.canDeliverAt(deliveryTime))
                .toList();
    }

    /**
     * Filter the restaurants by name
     *
     * @param name The name of the restaurant
     * @return A list of restaurants that have the name given in args
     */
    public static List<Restaurant> filterRestaurant(String name) {
        return filterRestaurant(name, null, null);
    }

    /**
     * Filter the restaurants by type of food
     *
     * @param typeOfFood The type of food the restaurant serves
     * @return A list of restaurants that serve the type of food given in args
     */
    public static List<Restaurant> filterRestaurant(TypeOfFood typeOfFood) {
        return filterRestaurant(null, typeOfFood, null);
    }

    /**
     * Filter the restaurants by delivery time
     *
     * @param deliveryTime The time the user wants the order to be delivered
     * @return A list of restaurants that can deliver at the time given in args
     */
    public static List<Restaurant> filterRestaurant(LocalDateTime deliveryTime) {
        return filterRestaurant(null, null, deliveryTime);
    }

    public static Duration getScheduleDurationForRestaurant(String restaurantId) throws NotFoundException {
        return getInstance().get(restaurantId).getScheduleDuration();
    }

    /**
     * Fill the restaurant manager with demo data
     */
    public void demo() {
        add(new Restaurant("1", "La Cafet", TypeOfFood.SNACKS));
        add(new Restaurant("2", "Le RU", TypeOfFood.CLASSIC));
        add(new Restaurant("3", "Restaurant Alban", TypeOfFood.CLASSIC));
        add(new Restaurant("4", "McAlban", TypeOfFood.FAST_FOOD));
        add(new Restaurant("5", "SushiRestaurant", TypeOfFood.CLASSIC));
    }
}
