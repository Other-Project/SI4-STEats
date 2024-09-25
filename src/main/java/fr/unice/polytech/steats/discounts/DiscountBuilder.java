package fr.unice.polytech.steats.discounts;

import fr.unice.polytech.steats.restaurant.MenuItem;

/**
 * Class used to create a {@link Discount}
 *
 * @author Team C
 */
public class DiscountBuilder {
    // Options
    private boolean appliesAfterOrder;
    private boolean oneTimeOffer;

    // Criteriums
    private int ordersAmount = 0;
    private int itemsAmount = 0;
    // TODO: Client's role

    // Discounts
    private double orderDiscount = 0;
    private double orderCredit = 0;
    private MenuItem[] freeItems;

    //region Options

    /**
     * The discount can't be applied to the current order and will take effect at the next order
     */
    public void appliesAfterOrder() {
        appliesAfterOrder = true;
    }

    /**
     * The discount can be applied to the current order
     */
    public void appliesDuringOrder() {
        appliesAfterOrder = false;
    }

    /**
     * This discount won't be available a second time
     */
    public void oneTimeOffer() {
        oneTimeOffer = true;
    }

    /**
     * This discount can be available a second time
     */
    public void keepOffer() {
        oneTimeOffer = false;
    }

    //endregion

    //region Criteriums

    /**
     * The quantity of orders needed to trigger the discount
     *
     * @param ordersAmount Quantity of orders since the last trigger of this discount
     */
    public void setOrdersAmount(int ordersAmount) {
        this.ordersAmount = ordersAmount;
    }

    /**
     * The quantity of items needed to be ordered to trigger the discount
     *
     * @param itemsAmount Quantity of items to order
     */
    public void setItemsAmount(int itemsAmount) {
        this.itemsAmount = itemsAmount;
    }

    //endregion

    //region Discounts

    /**
     * Provides a discount of a certain percentage on the order
     *
     * @param orderDiscount The discount percentage
     */
    public void setOrderDiscount(double orderDiscount) {
        this.orderDiscount = orderDiscount;
    }

    /**
     * Gives a discount of a certain amount of money
     *
     * @param orderCredit Amount of money to be credited
     */
    public void setOrderCredit(double orderCredit) {
        this.orderCredit = orderCredit;
    }

    /**
     * Add items to give
     *
     * @param freeItems The products to be gifted
     */
    public void setFreeItems(MenuItem... freeItems) {
        this.freeItems = freeItems;
    }

    //endregion

    /**
     * Creates a discount
     */
    public Discount build() {
        return null;
    }
}
