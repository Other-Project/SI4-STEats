package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.utils.AbstractManager;

/**
 * Manage restaurant discounts (stores, creates, deletes and gets them)
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class RestaurantDiscountManager extends AbstractManager<Discount> {
    private static final RestaurantDiscountManager INSTANCE = new RestaurantDiscountManager();

    private RestaurantDiscountManager() {
        super();
    }

    /**
     * Get the instance of the {@link RestaurantDiscountManager}
     */
    public static RestaurantDiscountManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Discount item) {
        add(item.getId(), item);
    }


    /**
     * Fill the manager with some demo data
     */
    public void demo() {
    }
}
