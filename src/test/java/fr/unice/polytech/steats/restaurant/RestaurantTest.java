package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.discounts.DiscountBuilder;
import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.order.SingleOrder;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    @Test
    public void testGetTypeOfFood() {
        Restaurant restaurant = new Restaurant("McDonald's", TypeOfFood.FAST_FOOD);
        assertEquals(TypeOfFood.FAST_FOOD, restaurant.getTypeOfFood());
    }

    @Test
    public void testGetOrders() {
        Restaurant restaurant = new Restaurant("McDonald's");
        Order order1 = new SingleOrder("John", LocalDateTime.of(2025, 12, 24, 11, 20), "Campus Sophia Tech", null);
        Order order2 = new SingleOrder("John", LocalDateTime.of(2025, 12, 24, 11, 20), "Campus Sophia Tech", null);
        restaurant.addOrder(order1);
        restaurant.addOrder(order2);
        List<Order> orders = restaurant.getOrders();
        assertEquals(2, orders.size());
        assertTrue(orders.contains(order1));
        assertTrue(orders.contains(order2));
    }

    @Test
    public void testRemoveDiscount() {
        Restaurant restaurant = new Restaurant("McDonald's");
        Discount discount = new DiscountBuilder().build();
        restaurant.addDiscount(discount);
        assertEquals(1, restaurant.discounts().size());
        restaurant.removeDiscount(discount);
        assertEquals(0, restaurant.discounts().size());
    }

    @Test
    public void testEquals() {
        Restaurant restaurant1 = new Restaurant("McDonald's");
        Restaurant restaurant2 = new Restaurant("McDonald's");
        Discount discount = new DiscountBuilder().build();
        restaurant1.addDiscount(discount);
        restaurant2.addDiscount(discount);
        MenuItem menuItem = new MenuItem("Big Mac", 5.0, null);
        restaurant1.addMenuItem(menuItem);
        restaurant2.addMenuItem(menuItem);
        assertEquals(restaurant1, restaurant2);
    }

    @Test
    public void testScheduleNotCoinciding() {
        Restaurant restaurant = new Restaurant("McDonald's");
        assertThrows(IllegalArgumentException.class, () -> restaurant.addSchedule(new Schedule(LocalTime.of(12, 10), Duration.ofMinutes(10), 5, DayOfWeek.FRIDAY)));
    }
}
