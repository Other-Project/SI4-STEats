package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.address.AddressManager;
import fr.unice.polytech.steats.NotFoundException;
import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.restaurant.Schedule;
import fr.unice.polytech.steats.users.Role;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.users.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupOrderTest {

    private Address address;

    @BeforeEach
    public void setUp() {
        RestaurantManager.getInstance().clear();
        AddressManager.getInstance().clear();
        GroupOrderManager.getInstance().clear();
        address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        AddressManager.getInstance().add(address.label(), address);
    }

    @Test
    void testGroupOrderAddress() {
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.now().plusDays(1), "Campus Sophia Tech", restaurant.getName());
        assertEquals(groupOrder.getAddress(), address);
    }

    @Test
    void testGroupOrderAddressNull() {
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () -> new GroupOrder(deliveryTime, null, null));
    }

    @Test
    void testGroupOrderRestaurant() {
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.now().plusDays(1), "Campus Sophia Tech", restaurant.getName());
        assertEquals(groupOrder.getRestaurant(), restaurant);
    }

    @Test
    void testGetItems() throws NotFoundException {
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        STEats steats = new STEats(new User("John", "JohnID", Role.EXTERNAL));
        Restaurant restaurant = new Restaurant("McDonald's");
        restaurant.addScheduleForPeriod(1,
                deliveryTime.minusHours(2).getDayOfWeek(), deliveryTime.minusHours(2).toLocalTime(),
                deliveryTime.getDayOfWeek(), deliveryTime.toLocalTime());
        MenuItem bigMac = new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10));
        MenuItem pizza = new MenuItem("Pizza", 15.0, Duration.ofMinutes(30));
        MenuItem cocaCola = new MenuItem("Coca-Cola", 2.5, Duration.ofMinutes(5));
        restaurant.addMenuItem(bigMac);
        restaurant.addMenuItem(pizza);
        restaurant.addMenuItem(cocaCola);
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        steats.createGroupOrder(deliveryTime, "Campus Sophia Tech", restaurant.getName());
        assertEquals(0, GroupOrderManager.getInstance().get(steats.getGroupCode()).getItems().size());
        steats.addMenuItem(bigMac);
        steats.addMenuItem(pizza);
        assertEquals(2, GroupOrderManager.getInstance().get(steats.getGroupCode()).getItems().size());
        steats.addMenuItem(cocaCola);
        assertEquals(3, GroupOrderManager.getInstance().get(steats.getGroupCode()).getItems().size());
    }

    @Test
    void testGetFullMenu() {
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        restaurant.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));
        restaurant.addMenuItem(new MenuItem("Pizza", 15.0, Duration.ofMinutes(30)));
        restaurant.addMenuItem(new MenuItem("Coca-Cola", 2.5, Duration.ofMinutes(5)));
        restaurant.addMenuItem(new MenuItem("Fries", 3.0, Duration.ofMinutes(5)));
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.now().plusDays(1), "Campus Sophia Tech", restaurant.getName());
        restaurant.addScheduleForPeriod(1,
                groupOrder.getDeliveryTime().minusHours(5).getDayOfWeek(), groupOrder.getDeliveryTime().minusHours(5).toLocalTime(), //from
                groupOrder.getDeliveryTime().plusHours(5).getDayOfWeek(), groupOrder.getDeliveryTime().plusHours(5).toLocalTime()); //to
        assertEquals(restaurant.getFullMenu(), groupOrder.getAvailableMenu());
    }

    @Test
    void testGetUsers() throws NotFoundException {
        User user1 = new User("John", "JohnID", Role.EXTERNAL);
        UserManager.getInstance().add(user1.getUserId(), user1);
        STEats steats1 = new STEats(user1);
        Restaurant restaurant = new Restaurant("McDonald's");
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        restaurant.addScheduleForPeriod(1,
                deliveryTime.minusHours(2).getDayOfWeek(), deliveryTime.minusHours(2).toLocalTime(),
                deliveryTime.getDayOfWeek(), deliveryTime.toLocalTime());
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        steats1.createGroupOrder(deliveryTime, "Campus Sophia Tech", restaurant.getName());
        assertEquals(1, GroupOrderManager.getInstance().get(steats1.getGroupCode()).getUsers().size());
        assertEquals(GroupOrderManager.getInstance().get(steats1.getGroupCode()).getUsers().getFirst(), user1);
    }

    @Test
    void testWrongSetDeliveryTime() throws NotFoundException {
        LocalDateTime deliveryTime = LocalDateTime.now().plusDays(1);
        STEats steats = new STEats(new User("John", "JohnID", Role.EXTERNAL));
        Restaurant restaurant = new Restaurant("McDonald's");
        restaurant.addScheduleForPeriod(1,
                deliveryTime.minusHours(2).getDayOfWeek(), deliveryTime.minusHours(2).toLocalTime(),
                deliveryTime.getDayOfWeek(), deliveryTime.toLocalTime());
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        steats.createGroupOrder(null, "Campus Sophia Tech", restaurant.getName());
        steats.changeDeliveryTime(deliveryTime);
        RestaurantManager.getInstance().get(restaurant.getName()).addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));
        steats.addMenuItem(new MenuItem("Big Mac", 5.0, Duration.ofMinutes(10)));
        GroupOrderManager.getInstance().get(steats.getGroupCode()).getRestaurant().addSchedule(new Schedule(LocalTime.now(), Duration.ofMinutes(30), 1, DayOfWeek.SATURDAY));
        String groupCode = steats.getGroupCode();
        GroupOrder groupOrder = GroupOrderManager.getInstance().get(groupCode);
        assertThrows(IllegalStateException.class, () -> groupOrder.setDeliveryTime(deliveryTime));
    }

    @Test
    void testCreateOrderWhenGroupOrderIsPaid() {
        Restaurant restaurant = new Restaurant("McDonald's");
        RestaurantManager.getInstance().add(restaurant.getName(), restaurant);
        GroupOrder groupOrder = new GroupOrder(LocalDateTime.now().plusDays(1), "Campus Sophia Tech", restaurant.getName());
        groupOrder.closeOrder();
        User user = new User("John", "JohnID", Role.EXTERNAL);
        String userId = user.getUserId();
        assertThrows(IllegalStateException.class, () -> groupOrder.createOrder(userId));
    }
}
