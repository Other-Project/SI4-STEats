package fr.unice.polytech.steats.discounts.restaurant;

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

//    /**
//     * Checks if the criteria are met
//     *
//     * @param order The current order
//     * @return True if the discount can be applied to the order
//     */
//    public boolean isApplicable(SingleOrder order) {
//        // TODO: Move this to DiscountServiceHelper
//        List<MenuItem> items = order.getItems().stream().map(item -> {
//            try {
//                return MenuItemServiceHelper.getMenuItem(item);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).toList();
//        List<SingleOrder> orders = null;
//        try {
//            orders = OrderServiceHelper.getOrdersByUserInRestaurant(order.userId(), order.restaurantId());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return items.size() >= criteria.currentOrderItemsAmount()
//                && (criteria.ordersAmount() <= 0 || (orders.size() + 1) % criteria.ordersAmount() == 0)
//                && (criteria.itemsAmount() <= 0 || orders.stream().mapToLong(o -> o.items().size()).sum() % criteria.itemsAmount() == 0)
//                && (criteria.clientRole().length == 0 || criteria.clientRole().contains(order.getUser().getRole()));
//    }

    /**
     * Can the discount be cumulated with other discounts
     */
    public boolean isStackable() {
        return options.stackable();
    }

    /**
     * Can the discount be applied to order that triggered it
     */
    public boolean canBeAppliedDirectly() {
        return !options.appliesAfterOrder();
    }

    /**
     * Items given by the discount
     */
    public List<String> freeItems() {
        return List.of(effects.freeItemIds());
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
     * Gets the price of the order once the discount has been applied
     *
     * @param price The price of the order
     */
    public double getNewPrice(double price) {
        return (price - effects.orderCredit()) * (1 - effects.orderDiscount());
    }

    /**
     * Gets the id of the discount
     */
    public String id() {
        return String.valueOf(hashCode());
    }
}
