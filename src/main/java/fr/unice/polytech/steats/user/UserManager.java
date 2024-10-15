package fr.unice.polytech.steats.user;

import fr.unice.polytech.steats.order.AbstractManager;

/**
 * Will manage users
 * It will be able to create, delete, update, get and store users
 *
 * @author Team C
 */
public class UserManager extends AbstractManager<User> {
    private static final UserManager INSTANCE = new UserManager();

    private UserManager() {
        super();
    }

    /**
     * Get the instance of the UserManager
     *
     * @return The instance of the UserManager
     */
    public static UserManager getInstance() {
        return INSTANCE;
    }
}
