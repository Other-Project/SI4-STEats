package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.Schedule;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GroupOrderTest {

    @BeforeEach
    public void setUp() {
        AddressManager.getInstance().clear();
    }

    @Test
    public void testGroupOrderAddress() {
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", null);
        assertEquals(groupOrder.getAddress(), address);
    }

    @Test
    public void testGroupOrderAddressNull() {
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", null);
        assertThrows(IllegalStateException.class, groupOrder::getAddress);
    }

    @Test
    public void testGroupOrderRestaurant() {
        Restaurant restaurant = new Restaurant("McDonald's");
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", restaurant);
        assertEquals(groupOrder.getRestaurant(), restaurant);
    }

    @Test
    public void testGetItems() throws NotFoundException {
        STEats steats = new STEats(new User("John", "JohnID", Role.EXTERNAL));
        steats.createGroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", new Restaurant("McDonald's"));
        assertEquals(GroupOrderManager.getInstance().get(steats.getGroupCode()).getItems().size(), 0);
        steats.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));
        steats.addMenuItem(new MenuItem("Pizza", 15.0, Duration.ofMinutes(30)));
        assertEquals(GroupOrderManager.getInstance().get(steats.getGroupCode()).getItems().size(), 2);
        steats.addMenuItem(new MenuItem("Coca-Cola", 2.5, Duration.ofMinutes(5)));
        assertEquals(GroupOrderManager.getInstance().get(steats.getGroupCode()).getItems().size(), 3);
    }

    @Test
    public void testGetFullMenu() {
        Restaurant restaurant = new Restaurant("McDonald's");
        restaurant.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));
        restaurant.addMenuItem(new MenuItem("Pizza", 15.0, Duration.ofMinutes(30)));
        restaurant.addMenuItem(new MenuItem("Coca-Cola", 2.5, Duration.ofMinutes(5)));
        restaurant.addMenuItem(new MenuItem("Fries", 3.0, Duration.ofMinutes(5)));
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", restaurant);
        assertEquals(restaurant.getFullMenu(), groupOrder.getAvailableMenu(LocalDateTime.of(2024, 10, 12, 10, 30)));
    }

    @Test
    public void testGetUsers() throws NotFoundException {
        User user1 = new User("John", "JohnID", Role.EXTERNAL);
        UserManager.getInstance().add(user1.getUserId(), user1);
        STEats steats1 = new STEats(user1);
        steats1.createGroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", new Restaurant("McDonald's"));
        assertEquals(GroupOrderManager.getInstance().get(steats1.getGroupCode()).getUsers().size(), 1);
        assertEquals(GroupOrderManager.getInstance().get(steats1.getGroupCode()).getUsers().getFirst(), user1);
    }

    @Test
    public void testWrongSetDeliveryTime() throws NotFoundException {
        STEats steats = new STEats(new User("John", "JohnID", Role.EXTERNAL));
        steats.createGroupOrder(null, "Campus Sophia Tech", new Restaurant("McDonald's"));
        steats.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(40)));
        GroupOrderManager.getInstance().get(steats.getGroupCode()).getRestaurant().addSchedule(new Schedule(LocalTime.of(9, 0), Duration.ofMinutes(30), 1, DayOfWeek.SATURDAY));
        assertThrows(IllegalStateException.class, () -> GroupOrderManager.getInstance().get(steats.getGroupCode()).setDeliveryTime(LocalDateTime.of(2024, 10, 12, 10, 15)));
    }

    @Test
    public void testCreateOrderWhenGroupOrderIsPaid() {
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.of(2024, 10, 12, 10, 30), "Campus Sophia Tech", new Restaurant("McDonald's"));
        groupOrder.closeOrder();
        assertThrows(IllegalStateException.class, () -> groupOrder.createOrder(new User("John", "JohnID", Role.EXTERNAL)));
    }
}
