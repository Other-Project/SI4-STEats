package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscountBuilderTest {

    private Restaurant restaurant;
    private SingleOrder order;


    @BeforeEach
    public void setUp() {
        AddressManager.getInstance().clear();
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add("Campus Sophia Tech", address);

        RestaurantManager.getInstance().clear();
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        restaurant = new Restaurant("Mcdo", TypeOfFood.FAST_FOOD);
        restaurant.addScheduleForPeriod(1,
                deliveryTime.minusHours(2).getDayOfWeek(), deliveryTime.minusHours(2).toLocalTime(),
                deliveryTime.getDayOfWeek(), deliveryTime.toLocalTime());
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);

        UserManager.getInstance().clear();
        User user = new User("Alban", "Alban", Role.STUDENT);
        UserManager.getInstance().add("Alban", user);

        order = new SingleOrder("Alban", deliveryTime, "Campus Sophia Tech", restaurant.getName());
    }


    @Test
    void testOneTimeOffer() {
        Discount discount = new DiscountBuilder("")
                .oneTimeOffer()
                .build();
        assertTrue(discount.getOptions().isExpired());
    }

    @Test
    void testSetItemsAmount() {
        Discount discount = new DiscountBuilder(restaurant.id())
                .setCurrentOrderItemsAmount(2)
                .build();
        restaurant.addDiscount(discount);
        restaurant.addOrder(order);
        order.addMenuItem(new MenuItem("1", "Burger", 5, Duration.ofMinutes(10), "1"));
        assertEquals(0, restaurant.availableDiscounts(order).size());
        order.addMenuItem(new MenuItem("2", "Burger", 5, Duration.ofMinutes(10), "1"));
        assertEquals(1, restaurant.availableDiscounts(order).size());
    }

    @Test
    void testExpiresAt() {
        Discount discountExpired = new DiscountBuilder("")
                .expiresAt(LocalDateTime.of(2024, 10, 12, 12, 0))
                .build();
        assertTrue(discountExpired.options().isExpired());
        Discount discount = new DiscountBuilder("")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        assertFalse(discount.options().isExpired());
    }

    @Test
    void testNeverExpires() {
        Discount discount = new DiscountBuilder("")
                .neverExpires()
                .build();
        assertFalse(discount.options().isExpired());
    }
}
