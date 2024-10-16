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

    public void fillForDemo() {
        User user1 = new User("John Doe", "123456", Role.STUDENT);
        User user2 = new User("Jane Doe", "654321", Role.STUDENT);
        User alban = new User("Alban Falcoz", "140403", Role.STUDENT);
        User theo = new User("Théo Lassauniere", "141103", Role.STUDENT);
        add(user1.getUserId(), user1);
        add(user2.getUserId(), user2);
        add(alban.getUserId(), alban);
        add(theo.getUserId(), theo);
    }
}
