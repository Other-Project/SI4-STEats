package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.NotFoundException;
import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.discounts.DiscountBuilder;
import fr.unice.polytech.steats.location.Address;
import fr.unice.polytech.steats.location.AddressManager;
import fr.unice.polytech.steats.schedule.Schedule;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestaurantTest {

    @BeforeEach
    public void setUp() {
        RestaurantManager.getInstance().clear();
        AddressManager.getInstance().clear();
        UserManager.getInstance().clear();
        GroupOrderManager.getInstance().clear();
        SingleOrderManager.getInstance().clear();
        AddressManager.getInstance().add("Campus SophiaTech", new Address("Campus SophiaTech", "930 Rt des Colles", "Biot", "06410", ""));
    }

    @Test
    void testGetTypeOfFood() {
        Restaurant restaurant = new Restaurant("McDonald's", TypeOfFood.FAST_FOOD);
        assertEquals(TypeOfFood.FAST_FOOD, restaurant.getTypeOfFood());
    }

    @Test
    void testGetOrders() {
        Restaurant restaurant = new Restaurant("McDonald's");
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        restaurant.addScheduleForPeriod(1,
                deliveryTime.minusHours(2).getDayOfWeek(), deliveryTime.minusHours(2).toLocalTime(),
                deliveryTime.getDayOfWeek(), deliveryTime.toLocalTime());
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        User user = new User("John", "JohnID", Role.EXTERNAL);
        UserManager.getInstance().add("JohnID", user);
        User user2 = new User("Jane", "JaneID", Role.EXTERNAL);
        UserManager.getInstance().add("JaneID", user2);
        STEats steats = new STEats(user);
        STEats steats2 = new STEats(user2);
        steats.createOrder(deliveryTime, "Campus Sophia Tech", restaurant.getName());
        steats2.createOrder(deliveryTime, "Campus Sophia Tech", restaurant.getName());
        List<Order> orders = restaurant.getOrders();
        assertEquals(2, orders.size());
    }

    @Test
    void testRemoveDiscount() {
        Restaurant restaurant = new Restaurant("McDonald's");
        Discount discount = new DiscountBuilder().build();
        restaurant.addDiscount(discount);
        assertEquals(1, restaurant.discounts().size());
        restaurant.removeDiscount(discount);
        assertEquals(0, restaurant.discounts().size());
    }

    @Test
    void testEquals() {
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
    void getOpeningTimes() {
        Restaurant restaurant = new Restaurant("");
        restaurant.addScheduleForPeriod(5, DayOfWeek.MONDAY, LocalTime.of(10, 0), DayOfWeek.MONDAY, LocalTime.of(14, 30));
        restaurant.addScheduleForPeriod(3, DayOfWeek.MONDAY, LocalTime.of(16, 0), DayOfWeek.MONDAY, LocalTime.of(17, 30));
        restaurant.addScheduleForPeriod(5, DayOfWeek.MONDAY, LocalTime.of(17, 30), DayOfWeek.MONDAY, LocalTime.of(19, 0));
        restaurant.addScheduleForPeriod(10, DayOfWeek.MONDAY, LocalTime.of(19, 0), DayOfWeek.TUESDAY, LocalTime.of(1, 0));
        restaurant.addScheduleForPeriod(10, DayOfWeek.FRIDAY, LocalTime.of(18, 0), DayOfWeek.MONDAY, LocalTime.of(3, 0));
        Map<DayOfWeek, List<OpeningTime>> openingByDays = Map.of(
                DayOfWeek.MONDAY, List.of(
                        new OpeningTime(LocalTime.of(0, 0), LocalTime.of(3, 0)),
                        new OpeningTime(LocalTime.of(10, 0), LocalTime.of(14, 30)),
                        new OpeningTime(LocalTime.of(16, 0), LocalTime.of(23, 59, 59))
                ),
                DayOfWeek.TUESDAY, List.of(new OpeningTime(LocalTime.of(0, 0), LocalTime.of(1, 0))),
                DayOfWeek.FRIDAY, List.of(new OpeningTime(LocalTime.of(18, 0), LocalTime.of(23, 59, 59))),
                DayOfWeek.SATURDAY, List.of(new OpeningTime(LocalTime.of(0, 0), LocalTime.of(23, 59, 59))),
                DayOfWeek.SUNDAY, List.of(new OpeningTime(LocalTime.of(0, 0), LocalTime.of(23, 59, 59)))
        );
        openingByDays.forEach((day, intervals) -> assertEquals(intervals, restaurant.getOpeningTimes(day), day + " opening times"));
    }

    @Test
    void setStatusOfOrder() throws NotFoundException {
        Restaurant restaurant = new Restaurant("McDonald's");
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        restaurant.addScheduleForPeriod(1,
                deliveryTime.minusHours(2).getDayOfWeek(), deliveryTime.minusHours(2).toLocalTime(),
                deliveryTime.getDayOfWeek(), deliveryTime.toLocalTime());
        RestaurantManager.getInstance().add("McDonald's", restaurant);
        Order order = new SingleOrder("1", deliveryTime, "Campus SophiaTech", restaurant.getName());
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(Status.PAID));
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(Status.DELIVERED));

        GroupOrder groupOrder = new GroupOrder(deliveryTime, "Campus SophiaTech", restaurant.getName());
        assertThrows(IllegalArgumentException.class, () -> groupOrder.setStatus(Status.PAID));
        assertThrows(IllegalArgumentException.class, () -> groupOrder.setStatus(Status.DELIVERED));

        User user = new User("Alex", "1", Role.STUDENT);
        UserManager.getInstance().add("1", user);
        STEats stEats = new STEats(user);
        String groupCode = stEats.createGroupOrder(deliveryTime, "Campus SophiaTech", restaurant.getName());
        stEats.payOrder();
        assertEquals(Status.PAID, GroupOrderManager.getInstance().get(groupCode).getOrders().getFirst().getStatus());
        assertThrows(IllegalArgumentException.class, () -> groupOrder.setStatus(Status.IN_PREPARATION));
        stEats.closeGroupOrder();
        GroupOrderManager.getInstance().get(groupCode).setStatus(Status.DELIVERED);
        assertEquals(Status.DELIVERED, GroupOrderManager.getInstance().get(groupCode).getStatus());
    }

    @Test
    void canHandle() throws NotFoundException {
        RestaurantManager.getInstance().clear();
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add("McDonald's", restaurant);
        restaurant.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));

        for (int i = 0; i < 4; i++) {
            restaurant.addSchedule(new Schedule(
                    LocalTime.now().plusHours(1).plus(Duration.ofMinutes(30).multipliedBy(i)),
                    Duration.ofMinutes(30),
                    1,
                    LocalDate.now().getDayOfWeek().plus(1)
            ));
            restaurant.addSchedule(new Schedule(
                    LocalTime.now().plusHours(1).plus(Duration.ofMinutes(30).multipliedBy(i)),
                    Duration.ofMinutes(30),
                    1,
                    LocalDate.now().getDayOfWeek()
            ));
            restaurant.addSchedule(new Schedule(
                    LocalTime.now().plusHours(1).plus(Duration.ofMinutes(30).multipliedBy(i)),
                    Duration.ofMinutes(30),
                    1,
                    LocalDate.now().getDayOfWeek().plus(2)
            ));
        }

        User user = new User("Alex", "2", Role.STUDENT);
        UserManager.getInstance().add("2", user);
        STEats stEats = new STEats(user);
        stEats.createOrder(LocalDateTime.now().plusHours(3), "Campus SophiaTech", restaurant.getName());

        stEats.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(6)));
        stEats.payOrder();

        for (int i = 1; i <= 5; i++) {
            SingleOrder iceCreamOrder = new SingleOrder("2", LocalDateTime.now().plusDays(1).plusHours(3), "Campus SophiaTech", restaurant.getName());
            iceCreamOrder.addMenuItem(new MenuItem("Ice Cream", 2.0, Duration.ofMinutes(5)));

            // Normally, max 6 ice cream if we don't consider a max order
            // 1st : max order = 3 > 1
            // 2nd : max order = 4 > 2
            // 3rd : max order = 4.5 > 3
            // 4th : max order = 4.8 > 4
            // 5th : max order = 5.0 = 5
            assertEquals(i != 5, restaurant.canHandle(iceCreamOrder, LocalDateTime.now().plusDays(1).plusHours(3)), "Can handle " + i + " ice cream");
        }
    }
}
