package fr.unice.polytech.steats.discounts.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.helpers.MenuItemServiceHelper;
import fr.unice.polytech.steats.models.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

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
        return getItemsAmount(order.orderedItems().values()) >= criteria.currentOrderItemsAmount()
                && (criteria.ordersAmount() <= 0 || (userOrders.size() + 1) % criteria.ordersAmount() == 0)
                && (criteria.itemsAmount() <= 0 || userOrders.stream().mapToLong(o -> getItemsAmount(o.orderedItems().values())).sum() % criteria.itemsAmount() == 0)
                && (criteria.clientRole().length == 0 || Arrays.stream(criteria.clientRole()).anyMatch(role -> role == user.role()));
    }

    private long getItemsAmount(Collection<Integer> itemQuantities) {
        LongAdder adder = new LongAdder();
        itemQuantities.parallelStream().forEach(adder::add);
        return adder.longValue();
    }

    /**
     * Gets the "value" of the discount (how much the user gains)
     *
     * @param price The sub price of the order
     * @implNote Used to compare discounts
     */
    public double value(double price) throws IOException {
        return effects.orderCredit()
                + getFreeItemsPrice()
                + price * effects.orderDiscount();
    }

    private double getFreeItemsPrice() throws IOException {
        double price = 0;
        for (String itemId : effects.freeItemIds())
            price += MenuItemServiceHelper.getMenuItem(itemId).price();
        return price;
    }

    /**
     * Gets the id of the discount
     */
    @JsonProperty("id")
    public String id() {
        return Integer.toUnsignedString(hashCode(), 16);
    }
}
