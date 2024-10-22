package fr.unice.polytech.steats.other;

import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractManagerTest {

    @BeforeEach
    public void setUp() {
        UserManager.getInstance().clear();
    }

    @Test
    void testAbstractManagerRemove() throws NotFoundException {
        User user = new User("John", "JohnID", Role.EXTERNAL);
        UserManager.getInstance().add(user.getName(), user);
        UserManager.getInstance().remove(user.getName());
        assertThrows(NotFoundException.class, () -> UserManager.getInstance().get(user.getName()));
    }

    @Test
    void testAbstractManagerGetAll() {
        User user1 = new User("John", "JohnID", Role.EXTERNAL);
        User user2 = new User("Jane", "JohnID", Role.EXTERNAL);
        UserManager.getInstance().add(user1.getName(), user1);
        UserManager.getInstance().add(user2.getName(), user2);
        assertTrue(UserManager.getInstance().getAll().contains(user1));
        assertTrue(UserManager.getInstance().getAll().contains(user2));
    }
}
