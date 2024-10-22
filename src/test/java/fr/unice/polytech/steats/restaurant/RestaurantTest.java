package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.discounts.DiscountBuilder;
import fr.unice.polytech.steats.order.*;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    private void addScheduleForPeriod(Restaurant restaurant, int nbPersons, DayOfWeek startDay, LocalTime startTime, DayOfWeek endDay, LocalTime endTime) {
        DayOfWeek day = startDay;
        for (LocalTime time = startTime; day != endDay || time.isBefore(endTime); time = time.plus(restaurant.getScheduleDuration())) {
            restaurant.addSchedule(new Schedule(time, restaurant.getScheduleDuration(), nbPersons, day));
            if (time.equals(LocalTime.of(0, 0).minus(restaurant.getScheduleDuration())))
                day = day.plus(1);
        }
    }

    @Test
    void testGetTypeOfFood() {
        Restaurant restaurant = new Restaurant("McDonald's", TypeOfFood.FAST_FOOD);
        assertEquals(TypeOfFood.FAST_FOOD, restaurant.getTypeOfFood());
    }

    @Test
    void testGetOrders() {
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        User user = new User("John", "JohnID", Role.EXTERNAL);
        UserManager.getInstance().add("JohnID", user);
        User user2 = new User("Jane", "JaneID", Role.EXTERNAL);
        UserManager.getInstance().add("JaneID", user2);
        STEats steats = new STEats(user);
        STEats steats2 = new STEats(user2);
        steats.createOrder(LocalDateTime.now().plusDays(1), "Campus Sophia Tech", restaurant.getName());
        steats2.createOrder(LocalDateTime.now().plusDays(1), "Campus Sophia Tech", restaurant.getName());
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
        addScheduleForPeriod(restaurant, 5, DayOfWeek.MONDAY, LocalTime.of(10, 0), DayOfWeek.MONDAY, LocalTime.of(14, 30));
        addScheduleForPeriod(restaurant, 3, DayOfWeek.MONDAY, LocalTime.of(16, 0), DayOfWeek.MONDAY, LocalTime.of(17, 30));
        addScheduleForPeriod(restaurant, 5, DayOfWeek.MONDAY, LocalTime.of(17, 30), DayOfWeek.MONDAY, LocalTime.of(19, 0));
        addScheduleForPeriod(restaurant, 10, DayOfWeek.MONDAY, LocalTime.of(19, 0), DayOfWeek.TUESDAY, LocalTime.of(1, 0));
        addScheduleForPeriod(restaurant, 10, DayOfWeek.FRIDAY, LocalTime.of(18, 0), DayOfWeek.MONDAY, LocalTime.of(3, 0));
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
        openingByDays.forEach((day, intervals) -> assertEquals(intervals, restaurant.getOpeningTimes(day)));
    }

    @Test
    void setStatusOfOrder() throws NotFoundException {
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add("McDonald's", restaurant);
        Order order = new SingleOrder("1", LocalDateTime.now().plusHours(3), "Campus SophiaTech", restaurant.getName());
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(Status.PAID));
        assertThrows(IllegalArgumentException.class, () -> order.setStatus(Status.DELIVERED));

        GroupOrder groupOrder = new GroupOrder(LocalDateTime.now().plusHours(3), "Campus SophiaTech", restaurant.getName());
        assertThrows(IllegalArgumentException.class, () -> groupOrder.setStatus(Status.PAID));
        assertThrows(IllegalArgumentException.class, () -> groupOrder.setStatus(Status.DELIVERED));

        User user = new User("Alex", "1", Role.STUDENT);
        UserManager.getInstance().add("1", user);
        STEats stEats = new STEats(user);
        String groupCode = stEats.createGroupOrder(LocalDateTime.now().plusHours(3), "Campus SophiaTech", restaurant.getName());
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

        SingleOrder singleOrder = new SingleOrder("1", LocalDateTime.now().plusHours(3), "Campus SophiaTech", restaurant.getName());

        User user = new User("Alex", "2", Role.STUDENT);
        UserManager.getInstance().add("2", user);
        STEats stEats = new STEats(user);
        stEats.createOrder(singleOrder.getDeliveryTime(), "Campus SophiaTech", restaurant.getName());

        stEats.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));
        stEats.payOrder();


        SingleOrder iceCreamOrder = new SingleOrder("2", LocalDateTime.now().plusDays(1).plusHours(3), "Campus SophiaTech", restaurant.getName());
        iceCreamOrder.addMenuItem(new MenuItem("Ice Cream", 2.0, Duration.ofMinutes(5)));
        // Normally, max 6 ice cream if we don't consider a max order
        assertTrue(restaurant.canHandle(iceCreamOrder, LocalDateTime.now().plusDays(1).plusHours(3))); // max order = 3 > 1
        restaurant.addOrder(iceCreamOrder);
        assertTrue(restaurant.canHandle(iceCreamOrder, LocalDateTime.now().plusDays(1).plusHours(3))); // max order = 4 > 2
        restaurant.addOrder(iceCreamOrder);
        assertTrue(restaurant.canHandle(iceCreamOrder, LocalDateTime.now().plusDays(1).plusHours(3))); // max order = 4.5 > 3
        restaurant.addOrder(iceCreamOrder);
        assertTrue(restaurant.canHandle(iceCreamOrder, LocalDateTime.now().plusDays(1).plusHours(3)));  // max order = 4.8 > 4
        restaurant.addOrder(iceCreamOrder);
        assertFalse(restaurant.canHandle(iceCreamOrder, LocalDateTime.now().plusDays(1).plusHours(3))); // max order = 5.0 = 5

    }
}
