package fr.unice.polytech.steats.discounts;

import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.restaurant.MenuItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A restaurant's discount with trigger criteria.
 *
 * @author Team C
 * @see DiscountBuilder
 */
public class Discount {
    private final DiscountBuilder.Options options;
    private final DiscountBuilder.Criteria criteria;
    private final DiscountBuilder.Discounts discounts;

    Discount(DiscountBuilder builder) {
        this.options = builder.getOptions();
        this.criteria = builder.getCriteria();
        this.discounts = builder.getDiscounts();
    }

    /**
     * Checks if the criteria are met
     * @param order The current order
     * @return True if the discount can be applied to the order
     */
    public boolean isApplicable(Order order) {
        List<MenuItem> items = order.getItems();
        // TODO: Other criteria need the User class
        return options.expirationDate.isAfter(LocalDateTime.now())
                && items.size() >= criteria.currentOrderItemsAmount;
    }
}
