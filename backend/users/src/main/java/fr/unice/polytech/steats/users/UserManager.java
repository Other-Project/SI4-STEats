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
                new User("John Doe", "123456", Role.STUDENT),
                new User("Jane Doe", "654321", Role.STUDENT),
                new User("Alban Falcoz", "140403", Role.STUDENT),
                new User("Théo Lassauniere", "141103", Role.STUDENT)
        ).forEach(user -> add(user.getUserId(), user));
    }
}
