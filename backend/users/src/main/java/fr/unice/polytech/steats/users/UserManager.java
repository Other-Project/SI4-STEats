package fr.unice.polytech.steats.users;

import fr.unice.polytech.steats.models.Role;
import fr.unice.polytech.steats.utils.AbstractManager;

import java.util.List;

/**
 * Manage users (store users, and allow to create, delete and get them)
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class UserManager extends AbstractManager<User> {
    private static final UserManager INSTANCE = new UserManager();

    private UserManager() {
        super();
    }

    /**
     * Get the instance of the {@link UserManager}
     */
    public static UserManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(User item) {
        add(item.getUserId(), item);
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        List.of(
                new User("John Doe", "JohnDoe", Role.STUDENT),
                new User("Jane Doe", "JaneDoe", Role.STUDENT),
                new User("Alban Falcoz", "AlbanFalcoz", Role.STUDENT),
                new User("Théo Lassauniere", "TheoLassauniere", Role.STUDENT)
        ).forEach(user -> add(user.getUserId(), user));
    }
}
