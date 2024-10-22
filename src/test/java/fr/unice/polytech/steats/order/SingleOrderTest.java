package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleOrderTest {

    @BeforeEach
    public void setUp() {
        AddressManager.getInstance().clear();
        UserManager.getInstance().clear();
    }

    @Test
    public void testSingleOrderAddress() {
        User user = new User("John", "JohnID", null);
        UserManager.getInstance().add(user.getName(), user);
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        SingleOrder singleOrder = new SingleOrder(user.getName(), null, "Campus Sophia Tech", null);
        assertEquals(singleOrder.getAddress(), address);
    }

    @Test
    public void testSingleOrderAddressNotFound() {
        User user = new User("John", "JohnID", null);
        UserManager.getInstance().add(user.getName(), user);

        SingleOrder singleOrder = new SingleOrder(user.getName(), null, "Campus Sophia Tech", null);
        assertThrows(IllegalStateException.class, singleOrder::getAddress);
    }
}
