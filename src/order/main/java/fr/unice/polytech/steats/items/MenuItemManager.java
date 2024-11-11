package fr.unice.polytech.steats.items;

import fr.unice.polytech.steats.NotFoundException;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;
import java.util.Objects;

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

    public List<MenuItem> getByRestaurant(String restaurantId){
        return getInstance().getAll().stream().filter(menuItem -> {
            try {
                return Objects.equals(menuItem.getRestaurant().getId(), restaurantId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

}
