package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.AbstractManager;

/**
 * Will manage single orders
 * It will be able to create, delete, update, get and store single orders
 *
 * @author Team C
 */
public class SingleOrderManager extends AbstractManager<SingleOrder> {
    private static final SingleOrderManager INSTANCE = new SingleOrderManager();

    private SingleOrderManager() {
        super();
    }

    /**
     * Get the instance of the SingleOrderManager
     *
     * @return The instance of the SingleOrderManager
     */
    public static SingleOrderManager getInstance() {
        return INSTANCE;
    }
}
