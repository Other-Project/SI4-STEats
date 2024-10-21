package fr.unice.polytech.steats.discounts;

import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.AddressManager;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.restaurant.TypeOfFood;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountBuilderTest {

    private Restaurant restaurant;
    private SingleOrder order;


    @BeforeEach
    public void setUp() {
        AddressManager.getInstance().clear();
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add("Campus Sophia Tech", address);

        RestaurantManager.getInstance().clear();
        restaurant = new Restaurant("Mcdo", TypeOfFood.FAST_FOOD);
        RestaurantManager.getInstance().add("Mcdo", restaurant);

        UserManager.getInstance().clear();
        User user = new User("Alban", "Alban", Role.STUDENT);
        UserManager.getInstance().add("Alban", user);

        order = new SingleOrder("Alban", LocalDateTime.of(2024, 1, 1, 12, 0), "Campus Sophia Tech", restaurant);
    }


    @Test
    public void testOneTimeOffer() {
        Discount discount = new DiscountBuilder()
                .oneTimeOffer()
                .build();
        assertTrue(discount.isExpired());
    }

    @Test
    public void testSetItemsAmount() {
        Discount discount = new DiscountBuilder()
                .setItemsAmount(2)
                .build();
        restaurant.addDiscount(discount);
        restaurant.addOrder(order);
        order.addMenuItem(new MenuItem("Burger", 5, Duration.ofMinutes(10)));
        order.addMenuItem(new MenuItem("Burger", 5, Duration.ofMinutes(10)));
        assertEquals(1, restaurant.availableDiscounts(order).size());
    }

    @Test
    public void testExpiresAt() {
        Discount discountExpired = new DiscountBuilder()
                .expiresAt(LocalDateTime.of(2024, 10, 12, 12, 0))
                .build();
        assertTrue(discountExpired.isExpired());
        Discount discount = new DiscountBuilder()
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        assertFalse(discount.isExpired());
    }

    @Test
    public void testNeverExpires() {
        Discount discount = new DiscountBuilder()
                .neverExpires()
                .build();
        assertFalse(discount.isExpired());
    }
}
