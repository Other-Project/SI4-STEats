package fr.unice.polytech.steats.utils;

import fr.unice.polytech.steats.NotFoundException;
import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.order.GroupOrderManager;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.users.Role;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.users.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractManagerTest {

    @BeforeEach
    public void setUp() {
        RestaurantManager.getInstance().clear();
        AddressManager.getInstance().clear();
        UserManager.getInstance().clear();
        GroupOrderManager.getInstance().clear();
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
