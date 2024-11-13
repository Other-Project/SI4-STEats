package fr.unice.polytech.steats.other;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.menuitem.MenuItem;
import fr.unice.polytech.steats.order.GroupOrderManager;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.users.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class STEatsTest {

    @BeforeEach
    public void setUp() {
        RestaurantManager.getInstance().clear();
        AddressManager.getInstance().clear();
        UserManager.getInstance().clear();
        GroupOrderManager.getInstance().clear();
    }

    @Test
    void testSTEatsGetUser() {
        User user = new User("Clara", "ClaraId", null);
        STEats steats = new STEats(user);
        assertEquals(steats.getUser(), user);
    }

    @Test
    void testGetFullMenu() {
        User user = new User("Clara", "ClaraId", null);
        UserManager.getInstance().add(user.getName(), user);
        STEats steats = new STEats(user);
        Restaurant restaurant = new Restaurant("Mcdo", null);
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        restaurant.addMenuItem(new MenuItem("Burger", 5, null));
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        steats.createOrder(null, address.label(), restaurant.getName());
        assertEquals(1, steats.getFullMenu().size());
        assertEquals("Burger", steats.getFullMenu().getFirst().getName());
    }

    @Test
    void testGetAllAddresses() {
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        Address address2 = new Address("Campus Carlone", "28 Avenue de Valrose", "Nice", "06108", "Bâtiment 2");
        AddressManager.getInstance().add(address2.label(), address2);
        STEats steats = new STEats();
        assertEquals(2, steats.getAddresses().size());
    }
}
