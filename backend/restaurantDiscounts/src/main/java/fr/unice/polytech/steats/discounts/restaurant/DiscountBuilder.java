package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.Criteria;
import fr.unice.polytech.steats.models.Effects;
import fr.unice.polytech.steats.models.Options;
import fr.unice.polytech.steats.models.Role;

import java.time.LocalDateTime;

/**
 * Class used to create a {@link Discount}
 *
 * @author Team C
 */
public class DiscountBuilder {

    private final String restaurantId;

    public DiscountBuilder(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    // Options
    static class OptionsConstructor {
        boolean stackable = false;
        boolean appliesAfterOrder = false;
        LocalDateTime expirationDate = null;
    }

    private final OptionsConstructor options = new OptionsConstructor();

    Options getOptions() {
        return new Options(options.stackable, options.appliesAfterOrder, options.expirationDate);
    }

    // Criteria
    static class CriteriaConstructor {
        int ordersAmount = 0;
        int currentOrderItemsAmount = 0;
        int itemsAmount = 0;
        Role[] clientRole = new Role[0];
    }

    private final CriteriaConstructor criteria = new CriteriaConstructor();

    Criteria getCriteria() {
        return new Criteria(criteria.ordersAmount, criteria.currentOrderItemsAmount, criteria.itemsAmount, criteria.clientRole);
    }


    // Discounts
    static class EffectsConstructor {
        double orderDiscount = 0;
        double orderCredit = 0;
        String[] freeItemIds = new String[0];
    }

    private final EffectsConstructor effects = new EffectsConstructor();

    Effects getEffects() {
        return new Effects(effects.orderDiscount, effects.orderCredit, effects.freeItemIds);
    }

    //region Options

    /**
     * The discount can be stacked with another discount
     */
    public DiscountBuilder stackable() {
        options.stackable = true;
        return this;
    }

    /**
     * The discount can't be stacked with other un-stackable discounts
     *
     * @apiNote The discount still can be stacked with stackable discounts
     */
    public DiscountBuilder unstackable() {
        options.stackable = false;
        return this;
    }

    /**
     * The discount can't be applied to the current order and will take effect at the next order
     */
    public DiscountBuilder appliesAfterOrder() {
        options.appliesAfterOrder = true;
        return this;
    }

    /**
     * The discount can be applied to the current order
     */
    public DiscountBuilder appliesDuringOrder() {
        options.appliesAfterOrder = false;
        return this;
    }

    /**
     * This discount won't be available a second time
     */
    public DiscountBuilder oneTimeOffer() {
        options.expirationDate = null;
        return this;
    }

    /**
     * This discount can be used (only once) in any order before the specified date
     *
     * @param expirationDate The discount expiration date
     */
    public DiscountBuilder expiresAt(LocalDateTime expirationDate) {
        options.expirationDate = expirationDate;
        return this;
    }

    /**
     * This discount can be used (only once) in any order
     */
    public DiscountBuilder neverExpires() {
        options.expirationDate = LocalDateTime.MAX;
        return this;
    }

    //endregion

    //region Criteria

    /**
     * The quantity of orders needed to trigger the discount
     *
     * @param ordersAmount Quantity of orders since the last trigger of this discount
     */
    public DiscountBuilder setOrdersAmount(int ordersAmount) {
        criteria.ordersAmount = ordersAmount;
        return this;
    }

    /**
     * The quantity of items needed to be purchased in the current order to trigger the discount
     *
     * @param itemsAmount Quantity of items to order
     */
    public DiscountBuilder setCurrentOrderItemsAmount(int itemsAmount) {
        criteria.currentOrderItemsAmount = itemsAmount;
        return this;
    }

    /**
     * The quantity of items needed to be ordered in total to trigger the discount
     *
     * @param itemsAmount Quantity of items to order
     */
    public DiscountBuilder setItemsAmount(int itemsAmount) {
        criteria.itemsAmount = itemsAmount;
        return this;
    }

    /**
     * The user needs to match one of these roles to be eligible for the discount
     *
     * @param roles A list of roles that are eligible
     */
    public DiscountBuilder setUserRoles(Role... roles) {
        criteria.clientRole = roles;
        return this;
    }

    //endregion

    //region Discounts

    /**
     * Provides a discount of a certain percentage on the order
     *
     * @param orderDiscount The discount percentage
     */
    public DiscountBuilder setOrderDiscount(double orderDiscount) {
        effects.orderDiscount = orderDiscount;
        return this;
    }

    /**
     * Gives a discount of a certain amount of money
     *
     * @param orderCredit Amount of money to be credited
     */
    public DiscountBuilder setOrderCredit(double orderCredit) {
        effects.orderCredit = orderCredit;
        return this;
    }

    /**
     * Add items to give
     *
     * @param freeItemIds Ids of the items to be gifted
     */
    public DiscountBuilder setFreeItems(String... freeItemIds) {
        effects.freeItemIds = freeItemIds;
        return this;
    }

    //endregion

    /**
     * Creates a discount
     */
    public Discount build() {
        return new Discount(restaurantId, this);
    }
}
