package fr.unice.polytech.steats.discounts.applied;

import fr.unice.polytech.steats.models.AppliedDiscount;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;
import java.util.Objects;

/**
 * Manage applied discounts (stores, creates, deletes and gets them)
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class AppliedDiscountManager extends AbstractManager<AppliedDiscount> {
    private static final AppliedDiscountManager INSTANCE = new AppliedDiscountManager();

    private AppliedDiscountManager() {
        super();
    }

    /**
     * Get the instance of the {@link AppliedDiscountManager}
     */
    public static AppliedDiscountManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(AppliedDiscount item) {
        add(item.id(), item);
    }

    /**
     * Bulk add applied discounts
     *
     * @param items The applied discounts to add
     */
    public void add(AppliedDiscount... items) {
        for (AppliedDiscount item : items)
            add(item);
    }


    /**
     * Get the discounts of an order
     *
     * @param appliedOrderId The id of the order
     */
    public List<AppliedDiscount> getAppliedDiscountsOfOrder(String appliedOrderId) {
        return getAll().stream().filter(discount -> Objects.equals(appliedOrderId, discount.appliedOrderId())).toList();
    }

    /**
     * Get the discounts of a user
     *
     * @param userId The id of the user
     */
    public List<AppliedDiscount> getAppliedDiscountsOfUser(String userId) {
        return getAll().stream().filter(discount -> Objects.equals(userId, discount.userId())).toList();
    }

    /**
     * Get the discounts of an order and a user
     *
     * @param appliedOrderId The id of the order
     * @param userId         The id of the user
     */
    public List<AppliedDiscount> getAppliedDiscountsOfOrderAndUser(String appliedOrderId, String userId) {
        return getAll().stream()
                .filter(discount -> Objects.equals(appliedOrderId, discount.appliedOrderId()) && Objects.equals(userId, discount.userId()))
                .toList();
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        String r1_8458abd1 = "8458abd1"; // 10% now
        String r1_226fc673 = "226fc673"; // 5% now
        String r2_47eba2da = "47eba2da"; // Item "3" now
        String r2_4374629 = "4374629"; // 0.5€ now
        String r2_625004e = "625004e"; // 1€ after

        String alban = "AlbanFalcoz";
        String jane = "JaneDoe";
        String john = "JohnDoe";

        add(new AppliedDiscount(r1_8458abd1, alban, "1", "1"));
        add(new AppliedDiscount(r1_8458abd1, alban, "3", "3"));
        add(new AppliedDiscount(r1_226fc673, alban, "5", "5"));
        add(new AppliedDiscount(r1_8458abd1, alban, "5", "5"));
        add(new AppliedDiscount(r1_8458abd1, alban, "9", "9"));

        add(new AppliedDiscount(r1_8458abd1, jane, "6", "6"));
        add(new AppliedDiscount(r2_625004e, jane, "8", "12"));
        add(new AppliedDiscount(r1_8458abd1, jane, "11", "11"));
        add(new AppliedDiscount(r2_47eba2da, jane, "12", "12"));
        add(new AppliedDiscount(r2_4374629, jane, "12", "12"));

        add(new AppliedDiscount(r2_625004e, john, "4", "7"));
        add(new AppliedDiscount(r2_625004e, john, "7", null));
    }
}
