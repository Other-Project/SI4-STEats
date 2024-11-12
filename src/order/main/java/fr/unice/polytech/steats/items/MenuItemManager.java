package fr.unice.polytech.steats.items;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.utils.AbstractManager;
import fr.unice.polytech.steats.utils.NotFoundException;

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
    public List<MenuItem> getByRestaurant(String restaurantId){
        return getInstance().getAll().stream()
                .filter(menuItem -> {
                    try {
                        return Objects.equals(menuItem.getRestaurant().getId(), restaurantId);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    @Override
    public void add(MenuItem item) {
        super.add(item.getId(), item);
    }
}
