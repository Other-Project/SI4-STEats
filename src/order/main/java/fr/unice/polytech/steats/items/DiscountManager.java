package fr.unice.polytech.steats.items;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.utils.AbstractManager;

public class DiscountManager extends AbstractManager<Discount> {

    private static final DiscountManager INSTANCE = new DiscountManager();

    private DiscountManager() {
        super();
    }

    /**
     * Get the instance of the UserManager
     *
     * @return The instance of the UserManager
     */
    public static DiscountManager getInstance() {
        return INSTANCE;
    }
}
