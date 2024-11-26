package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.Role;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.models.User;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
     * Get the discounts of a restaurant
     *
     * @param restaurantId The id of the restaurant
     */
    public List<Discount> getDiscountsOfRestaurant(String restaurantId) {
        return getAll().stream().filter(discount -> Objects.equals(restaurantId, discount.restaurantId())).toList();
    }

    /**
     * Get the applicable discounts for an order
     *
     * @param order  The order
     * @param user   The user
     * @param orders The user's orders
     */
    public List<Discount> getApplicableDiscounts(SingleOrder order, User user, List<SingleOrder> orders) throws IOException {
        List<Discount> discounts = new ArrayList<>();
        Iterator<Discount> it = getDiscountsOfRestaurant(order.restaurantId())
                .parallelStream()
                .filter(discount -> discount.isApplicable(order, user, orders))
                .iterator();

        Discount bestDiscount = null;
        double bestValue = -1;
        while (it.hasNext()) {
            Discount discount = it.next();
            double value = discount.value(order.subPrice());
            if (discount.options().stackable()) discounts.add(discount);
            else if (value > bestValue) {
                bestDiscount = discount;
                bestValue = value;
            }
        }
        if (bestDiscount != null) discounts.add(bestDiscount);

        return discounts;
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        add(new DiscountBuilder("1").setOrderDiscount(0.05).setOrdersAmount(3).appliesDuringOrder().build());
        add(new DiscountBuilder("1").setOrderDiscount(0.1).setUserRoles(Role.STUDENT).appliesDuringOrder().stackable().build());
        add(new DiscountBuilder("2").setFreeItems("3").setOrdersAmount(3).appliesDuringOrder().stackable().build());
        add(new DiscountBuilder("2").setOrderCredit(0.5).setCurrentOrderItemsAmount(3).appliesDuringOrder().unstackable().build());
        add(new DiscountBuilder("2").setOrderCredit(1).setCurrentOrderItemsAmount(4).appliesAfterOrder().unstackable().build());
    }
}
