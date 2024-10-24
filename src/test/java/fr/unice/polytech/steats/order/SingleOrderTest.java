package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SingleOrderTest {

    @BeforeEach
    public void setUp() {
        RestaurantManager.getInstance().clear();
        AddressManager.getInstance().clear();
        UserManager.getInstance().clear();
        GroupOrderManager.getInstance().clear();
    }

    @Test
    void testSingleOrderAddress() {
        User user = new User("John", "JohnID", null);
        UserManager.getInstance().add(user.getName(), user);
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        SingleOrder singleOrder = new SingleOrder(user.getName(), null, "Campus Sophia Tech", restaurant.getName());
        assertEquals(singleOrder.getAddress(), address);
    }

    @Test
    void testSingleOrderAddressNotFound() {
        User user = new User("John", "JohnID", null);
        UserManager.getInstance().add(user.getName(), user);
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        String userName = user.getName();
        String restaurantName = restaurant.getName();
        assertThrows(IllegalArgumentException.class, () -> new SingleOrder(userName, null, "Campus Sophia Tech", restaurantName));

    }
}
