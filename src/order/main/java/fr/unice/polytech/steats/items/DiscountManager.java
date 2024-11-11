package fr.unice.polytech.steats.items;

import fr.unice.polytech.steats.NotFoundException;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;
import java.util.Objects;

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

    public List<Discount> getByRestaurant(String restaurantId) {
        return getInstance().getAll().stream().filter(discount -> {
            try {
                return Objects.equals(discount.getRestaurant().getId(), restaurantId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
