package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscountBuilderTest {

    private User user;
    private Restaurant restaurant;


    @BeforeEach
    public void setUp() {
        restaurant = new Restaurant("Mcdo", "Mcdo", TypeOfFood.FAST_FOOD, Duration.ofMinutes(30));
        user = new User("Alban", "Alban", Role.STUDENT);
    }


    @Test
    void testExpiry() {
        SingleOrder order = new SingleOrder("orderId", user.userId(), null, LocalDateTime.now().plusHours(2), "Campus SophiaTech", restaurant.id(),
                Status.INITIALISED, Map.of(), Map.of(), List.of(), Duration.ZERO, LocalDateTime.now(), 0, 0);

        Discount discount = new DiscountBuilder(restaurant.id()).expiresAt(LocalDateTime.now().minusDays(1)).build();
        assertTrue(discount.options().isExpired());
        assertFalse(discount.isApplicable(order, user, List.of()));

        discount = new DiscountBuilder(restaurant.id()).expiresAt(LocalDateTime.now().plusDays(1)).build();
        assertFalse(discount.options().isExpired());
        assertTrue(discount.isApplicable(order, user, List.of()));

        discount = new DiscountBuilder(restaurant.id()).neverExpires().build();
        assertFalse(discount.options().isExpired());
        assertTrue(discount.isApplicable(order, user, List.of()));
    }

    @Test
    void testSetItemsAmount() {
        Discount discount = new DiscountBuilder(restaurant.id()).setCurrentOrderItemsAmount(2).build();
        RestaurantDiscountManager.getInstance().add(discount);

        SingleOrder order = new SingleOrder("orderId", user.userId(), null, LocalDateTime.now().plusHours(2), "Campus SophiaTech", restaurant.id(),
                Status.INITIALISED, Map.of("1", 1), Map.of("1", 1), List.of(), Duration.ZERO, LocalDateTime.now(), 0, 0);
        assertFalse(discount.isApplicable(order, user, List.of()));
        order = new SingleOrder("orderId", user.userId(), null, LocalDateTime.now().plusHours(2), "Campus SophiaTech", restaurant.id(),
                Status.INITIALISED, Map.of("1", 2), Map.of("1", 2), List.of(), Duration.ZERO, LocalDateTime.now(), 0, 0);
        assertTrue(discount.isApplicable(order, user, List.of()));
    }

    @Test
    void testExpiresAt() {
        Discount discountExpired = new DiscountBuilder(restaurant.id())
                .expiresAt(LocalDateTime.of(2024, 10, 12, 12, 0))
                .build();
        assertTrue(discountExpired.options().isExpired());
        Discount discount = new DiscountBuilder(restaurant.id())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        assertFalse(discount.options().isExpired());
    }

    @Test
    void testNeverExpires() {
        Discount discount = new DiscountBuilder(restaurant.id())
                .neverExpires()
                .build();
        assertFalse(discount.options().isExpired());
    }
}
