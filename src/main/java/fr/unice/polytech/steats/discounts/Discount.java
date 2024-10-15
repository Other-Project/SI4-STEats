package fr.unice.polytech.steats.discounts;

import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;

import java.time.LocalDateTime;
import java.util.Arrays;
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
     *
     * @param order The current order
     * @return True if the discount can be applied to the order
     */
    public boolean isApplicable(SingleOrder order) {
        List<MenuItem> items = order.getItems();
        return items.size() >= criteria.currentOrderItemsAmount
                && (criteria.ordersAmount <= 0 || (order.getUser().getOrders().size() + 1) % criteria.ordersAmount == 0)
                && (criteria.itemsAmount <= 0 || order.getUser().getOrders().stream().mapToLong(o -> o.getItems().size()).sum() % criteria.itemsAmount == 0)
                && (criteria.clientRole == null || criteria.clientRole.contains(order.getUser().getRole()));
    }

    /**
     * Can the discount be cumulated with other discounts
     */
    public boolean isStackable() {
        return options.stackable;
    }

    /**
     * Can the discount be applied to order that triggered it
     */
    public boolean canBeAppliedDirectly() {
        return !options.appliesAfterOrder;
    }

    /**
     * Items given by the discount
     */
    public List<MenuItem> freeItems() {
        return List.of(discounts.freeItems);
    }

    /**
     * Gets the "value" of the discount (how much the user gains)
     *
     * @param price The sub price of the order
     * @implNote Used to compare discounts
     */
    public double value(double price) {
        return discounts.orderCredit
                + Arrays.stream(discounts.freeItems).mapToDouble(MenuItem::getPrice).sum()
                + price * discounts.orderDiscount;
    }

    /**
     * Gets the price of the order once the discount has been applied
     *
     * @param price The price of the order
     */
    public double getNewPrice(double price) {
        return (price - discounts.orderCredit) * (1 - discounts.orderDiscount);
    }

    /**
     * Is the discount expired
     */
    public boolean isExpired() {
        return options.expirationDate == null || options.expirationDate.isBefore(LocalDateTime.now());
    }
}
