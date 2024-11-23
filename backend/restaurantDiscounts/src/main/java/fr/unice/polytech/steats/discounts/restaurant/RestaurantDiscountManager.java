package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.Role;
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
        add(item.id(), item);
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        add(new DiscountBuilder("1").setOrderDiscount(0.05).setOrdersAmount(10).appliesDuringOrder().build());
        add(new DiscountBuilder("1").setOrderDiscount(0.1).setUserRoles(Role.STUDENT).appliesDuringOrder().stackable().build());
        add(new DiscountBuilder("2").setFreeItems("2").setOrdersAmount(10).appliesDuringOrder().stackable().build());
        add(new DiscountBuilder("2").setOrderCredit(0.5).setCurrentOrderItemsAmount(3).appliesDuringOrder().unstackable().build());
        add(new DiscountBuilder("2").setOrderCredit(1).setCurrentOrderItemsAmount(4).appliesAfterOrder().unstackable().build());
    }
}
