package fr.unice.polytech.steats.discounts.applied;

import fr.unice.polytech.steats.models.AppliedDiscount;
import fr.unice.polytech.steats.utils.AbstractManager;

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
     * Fill the manager with some demo data
     */
    public void demo() {
    }
}
