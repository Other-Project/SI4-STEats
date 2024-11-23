package fr.unice.polytech.steats.discounts.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.models.*;

import java.util.Arrays;
import java.util.List;

/**
 * A restaurant's discount with trigger criteria.
 *
 * @author Team C
 * @see DiscountBuilder
 */
public record Discount(String restaurantId, Options options, Criteria criteria, Effects effects) {

    Discount(String restaurantId, DiscountBuilder builder) {
        this(restaurantId, builder.getOptions(), builder.getCriteria(), builder.getEffects());
    }

    /**
     * Checks if the criteria are met
     *
     * @param order The current order
     * @return True if the discount can be applied to the order
     */
    public boolean isApplicable(SingleOrder order, User user, List<SingleOrder> userOrders) {
        return order.items().size() >= criteria.currentOrderItemsAmount()
                && (criteria.ordersAmount() <= 0 || (userOrders.size() + 1) % criteria.ordersAmount() == 0)
                && (criteria.itemsAmount() <= 0 || userOrders.stream().mapToLong(o -> o.items().size()).sum() % criteria.itemsAmount() == 0)
                && (criteria.clientRole().length == 0 || Arrays.stream(criteria.clientRole()).anyMatch(role -> role == user.role()));
    }

    /**
     * Gets the "value" of the discount (how much the user gains)
     *
     * @param price The sub price of the order
     * @implNote Used to compare discounts
     */
    public double value(double price) {
        return effects.orderCredit()
                //+ Arrays.stream(discounts.freeItemIds).mapToDouble(MenuItem::price).sum() // TODO : fix this
                + price * effects.orderDiscount();
    }

    /**
     * Gets the id of the discount
     */
    @JsonProperty("id")
    public String id() {
        return Integer.toUnsignedString(hashCode(), 16);
    }
}
