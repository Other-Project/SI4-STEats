package fr.unice.polytech.steats.other;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.AddressManager;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class STEatsTest {

    @BeforeEach
    public void setUp() {
        AddressManager.getInstance().clear();
        UserManager.getInstance().clear();
    }

    @Test
    public void testSTEatsGetUser() {
        User user = new User("Clara", "ClaraId", null);
        STEats steats = new STEats(user);
        assert steats.getUser().equals(user);
    }

    @Test
    public void testGetFullMenu() {
        User user = new User("Clara", "ClaraId", null);
        UserManager.getInstance().add(user.getName(), user);
        STEats steats = new STEats(user);
        Restaurant restaurant = new Restaurant("Mcdo", null);
        restaurant.addMenuItem(new MenuItem("Burger", 5, null));
        steats.createOrder(null, null, restaurant);
        assert steats.getFullMenu().size() == 1;
        assert steats.getFullMenu().getFirst().getName().equals("Burger");
    }

    @Test
    public void testGetAllAddresses() {
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        Address address2 = new Address("Campus Carlone", "28 Avenue de Valrose", "Nice", "06108", "Bâtiment 2");
        AddressManager.getInstance().add(address2.label(), address2);
        STEats steats = new STEats();
        assert steats.getAddresses().size() == 2;
    }
}
