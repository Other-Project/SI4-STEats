package fr.unice.polytech.steats.discount;

import fr.unice.polytech.steats.restaurant.MenuItem;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * A restaurant's discount with trigger criteria.
 *
 * @author Team C
 * @see DiscountBuilder
 */
public class Discount {

    private String id;
    private final DiscountBuilder.Options options;
    private final DiscountBuilder.Criteria criteria;
    private final DiscountBuilder.Discounts discounts;

    Discount(DiscountBuilder builder) {
        this.id = UUID.randomUUID().toString();
        this.options = builder.getOptions();
        this.criteria = builder.getCriteria();
        this.discounts = builder.getDiscounts();
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
//            orders = OrderServiceHelper.getOrdersByUserInRestaurant(order.getUser().getUserId(), order.getRestaurantId());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return items.size() >= criteria.currentOrderItemsAmount
//                && (criteria.ordersAmount <= 0 || (orders.size() + 1) % criteria.ordersAmount == 0)
//                && (criteria.itemsAmount <= 0 || orders.stream().mapToLong(o -> o.getItems().size()).sum() % criteria.itemsAmount == 0)
//                && (criteria.clientRole == null || criteria.clientRole.contains(order.getUser().getRole()));
//    }

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
     * Gets the id of the discount
     */
    public String getId() {
        return id;
    }

    /**
     * Is the discount expired
     */
    public boolean isExpired() {
        return (options.expirationDate == null && !options.appliesAfterOrder)
                || (options.expirationDate != null && options.expirationDate.isBefore(LocalDateTime.now()));
    }
}
