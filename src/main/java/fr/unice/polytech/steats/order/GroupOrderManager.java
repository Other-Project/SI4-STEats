package fr.unice.polytech.steats.order;

/**
 * Will manage group orders
 * It will be able to create, delete, update, get and store group orders
 *
 * @author Team C
 */
public class GroupOrderManager extends AbstractManager<GroupOrder> {
    private static final GroupOrderManager INSTANCE = new GroupOrderManager();

    private GroupOrderManager() {
        super();
    }

    /**
     * Get the instance of the GroupOrderManager
     *
     * @return The instance of the GroupOrderManager
     */
    public static GroupOrderManager getInstance() {
        return INSTANCE;
    }
}

