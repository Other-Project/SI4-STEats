package fr.unice.polytech.steats.menuitem;

import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S6548")
public class MenuItemManager extends AbstractManager<MenuItem> {
    private static final MenuItemManager INSTANCE = new MenuItemManager();

    private MenuItemManager() {
        super();
    }

    /**
     * Get the instance of the UserManager
     *
     * @return The instance of the UserManager
     */
    public static MenuItemManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get all the menu items of a restaurant
     *
     * @param restaurantId The id of the restaurant
     */
    public List<MenuItem> getByRestaurant(String restaurantId) {
        return getInstance().getAll()
                .stream()
                .filter(menuItem -> Objects.equals(menuItem.restaurantId(), restaurantId))
                .toList();
    }

    @Override
    public void add(MenuItem item) {
        super.add(item.id(), item);
    }

    public void demo() {
        add(new MenuItem("1", "Pizza", 10.00, Duration.ofMinutes(40), "1"));
        add(new MenuItem("2", "Pasta", 12.50, Duration.ofMinutes(15), "1"));
        add(new MenuItem("3", "Salad", 8.00, Duration.ofMinutes(3), "2"));
        add(new MenuItem("4", "Burger", 15.00, Duration.ofMinutes(10), "3"));
        add(new MenuItem("5", "Sushi", 20.00, Duration.ofMinutes(15), "4"));
        add(new MenuItem("6", "Chocolate ice cream", 5.00, Duration.ofMinutes(5), "1"));
        add(new MenuItem("7", "Vanilla ice cream", 5.00, Duration.ofMinutes(5), "1"));
        add(new MenuItem("8", "Strawberry ice cream", 5.00, Duration.ofMinutes(5), "1"));
        add(new MenuItem("9", "Mint ice cream", 5.00, Duration.ofMinutes(5), "1"));
        add(new MenuItem("10", "Pistachio ice cream", 5.00, Duration.ofMinutes(5), "1"));

    }
}
