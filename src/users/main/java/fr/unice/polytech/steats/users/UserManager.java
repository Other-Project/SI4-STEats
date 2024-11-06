package fr.unice.polytech.steats.users;

import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;

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
        List.of(
                new User("John Doe", "123456", Role.STUDENT),
                new User("Jane Doe", "654321", Role.STUDENT),
                new User("Alban Falcoz", "140403", Role.STUDENT),
                new User("Théo Lassauniere", "141103", Role.STUDENT)
        ).forEach(user -> add(user.getUserId(), user));
    }

    @Override
    public void add(User item) {
        add(item.getUserId(), item);
    }
}
