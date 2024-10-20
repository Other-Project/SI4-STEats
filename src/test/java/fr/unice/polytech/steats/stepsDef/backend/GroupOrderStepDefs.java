package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.*;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.restaurant.Schedule;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GroupOrderStepDefs {

    STEatsController stEatsController = new STEatsController();
    Map<String, STEats> steatsMap = new HashMap<>();

    @Before
    public void before() {
        GroupOrderManager.getInstance().clear();
        UserManager.getInstance().clear();
    }

    @Given("A group order with the group code {string} from the restaurant {string} and to deliver for {string} at {string}")
    public void a_group_order_is_created(String groupCode, String restaurant, String deliveryTime, String address) {
        GroupOrderManager.getInstance().add(groupCode, new GroupOrder(groupCode, LocalDateTime.parse(deliveryTime), new Address(address, "1", "1", "1"), new Restaurant(restaurant)));
    }

    @Given("The user named {string} with the id {string} is logged in")
    public void theUserWithTheIdIsLoggedIn(String name, String userId) throws NotFoundException {
        User user = new User(name, userId, Role.STUDENT);
        UserManager.getInstance().add(userId, user);
        steatsMap.put(name, stEatsController.logging(userId));
    }

    @When("{string} joins the group order with the group code {string}")
    public void the_user_joins_the_group_order(String name, String groupCode) throws NotFoundException {
        steatsMap.get(name).joinGroupOrder(groupCode);
    }

    @Then("{string} is added to the group order with the group code {string}")
    public void the_user_is_added_to_the_group_order(String name, String groupCode) throws NotFoundException {
        assertEquals(1, GroupOrderManager.getInstance().get(groupCode).getOrders().size());
        assertEquals(steatsMap.get(name).getOrder(), GroupOrderManager.getInstance().get(groupCode).getOrders().get(0));
        assertEquals(name, UserManager.getInstance().get(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                        .map(SingleOrder::getUserId)
                        .toList()
                        .get(0))
                .getName());
    }

    @When("{string} adds the item named {string} with a price of {double} to the group order")
    public void theUserWithTheIdAddsTheItemNamedWithAPriceOfToTheGroupOrderWithTheGroupCode(String name, String menuItem, double price) {
        steatsMap.get(name).addMenuItem(new MenuItem(menuItem, price, Duration.ofMinutes(10)));
    }

    @Then("The item with named {string} with a price of {double} is added to the order of {string} in the group order with the group code {string}")
    public void theItemWithNamedIsAddedToTheOrderOfTheUserWithTheIdInTheGroupOrderWithTheGroupCode(String menuItem, double price, String name, String groupCode) throws NotFoundException {
        assertEquals(price, steatsMap.get(name).getTotalPrice(), 0.01);
        assertTrue(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(SingleOrder::getItems)
                .flatMap(Collection::stream)
                .anyMatch(item -> Objects.equals(item.getName(), menuItem)));
        assertEquals(price, GroupOrderManager.getInstance().get(groupCode).getPrice(), 0.01);
        assertTrue(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> {
                    try {
                        return UserManager.getInstance().get(order.getUserId()).getName();
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList().contains(name));
    }

    @When("{string} pays")
    public void theUserWithTheIdPay(String name) throws NotFoundException {
        assertTrue(steatsMap.get(name).payOrder());
    }

    @Then("The order of {string} is payed")
    public void theOrderOfTheUserWithTheIdIsPayed(String name) {
        assertSame(Status.PAID, steatsMap.get(name).getOrder().getStatus());
    }

    @And("The order is added to the history of {string}")
    public void theOrderIsAddedToTheHistoryOfTheUserWithTheId(String name) throws NotFoundException {
        assertEquals(1, UserManager.getInstance().get(steatsMap.get(name).getOrder().getUserId()).getOrders().size());
    }

    @And("The group order with the group code {string} is payed")
    public void theGroupOrderIsPayed(String groupCode) throws NotFoundException {
        assertSame(Status.PAID, GroupOrderManager.getInstance().get(groupCode).getStatus());
    }

    @And("{string} can close the group order")
    public void heCanCloseTheGroupOrder(String name) throws NotFoundException {
        assertTrue(steatsMap.get(name).canCloseGroupOrder());
        assertDoesNotThrow(() -> steatsMap.get(name).closeGroupOrder());
    }


    @And("{string} can't close the group order")
    public void theUserWithTheIdCanTCloseTheGroupOrder(String name) throws NotFoundException {
        assertFalse(steatsMap.get(name).canCloseGroupOrder());
        assertThrows(IllegalStateException.class, () -> steatsMap.get(name).closeGroupOrder());
    }

    @When("{string} close the group order")
    public void theUserWithTheIdCloseTheGroupOrder(String name) throws NotFoundException {
        steatsMap.get(name).closeGroupOrder();
    }

    @Then("the group order with the id {string} is closed")
    public void theGroupOrderIsClosed(String groupeCode) throws NotFoundException {
        assertSame(Status.PAID, GroupOrderManager.getInstance().get(groupeCode).getStatus());
    }

    @When("{string} creates a group order from the restaurant {string} and to deliver for {string} at {string}")
    public void createsAGroupOrderFromTheRestaurantAndToDeliverForAt(String name, String restaurant, String time, String adress) {
        assertNotNull(steatsMap.get(name).createGroupOrder(LocalDateTime.parse(time), new Address(adress, "1", "1", "1"), new Restaurant(restaurant)));
    }

    @Then("{string} receives a group code")
    public void receiveAGroupCode(String name) {
        assertNotNull(steatsMap.get(name).getGroupCode());
    }

    @When("{string} creates a group order from the restaurant {string} and to deliver at {string}")
    public void createsAGroupOrderFromTheRestaurantAndToDeliverAt(String name, String restaurant, String adress) {
        steatsMap.get(name).createGroupOrder(null, new Address(adress, "1", "1", "1"), new Restaurant(restaurant));
    }

    @And("{string} can't change the delivery time to {string} to the group order")
    public void canTChangeTheDeliveryTimeToToTheGroupOrder(String name, String time) {
        assertThrows(IllegalStateException.class, () -> steatsMap.get(name).changeDeliveryTime(LocalDateTime.parse(time)));
    }

    @And("{string} can add {string} as delivery time to the group order")
    public void canAddAsDeliveryTimeToTheGroupOrder(String name, String time) {
        assertDoesNotThrow(() -> steatsMap.get(name).changeDeliveryTime(LocalDateTime.parse(time)));
    }

    @Given("A restaurant named {string} with the following schedules :")
    public void aRestaurantNamedWithTheFollowingSchedules(String restaurant, List<Map<String, String>> schedules) {
        RestaurantManager.getInstance().add(restaurant, new Restaurant(restaurant));
        schedules.forEach(schedule -> {
            try {
                RestaurantManager.getInstance().get(restaurant).addSchedule(new Schedule(
                        LocalTime.parse(schedule.get("start")),
                        Duration.ofMinutes(Long.parseLong(schedule.get("duration"))),
                        Integer.parseInt(schedule.get("capacity")),
                        DayOfWeek.valueOf(schedule.get("day"))
                ));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Given("The restaurant named {string} have the following menu :")
    public void theRestaurantNamedHaveTheFollowingMenu(String restaurant, List<Map<String, String>> menu) {
        menu.forEach(item -> {
            try {
                RestaurantManager.getInstance().get(restaurant).addMenuItem(new MenuItem(item.get("name"), Double.parseDouble(item.get("price")), Duration.ofMinutes(Long.parseLong(item.get("preparationTime")))));
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Given("A group order with the group code {string} from the restaurant {string} at {string}")
    public void aGroupOrderWithTheGroupCodeFromTheRestaurantAt(String groupCode, String restaurant, String adress) throws NotFoundException {
        GroupOrderManager.getInstance().add(groupCode, new GroupOrder(groupCode, null, new Address(adress, "1", "1", "1"), RestaurantManager.getInstance().get(restaurant)));
    }

    @Given("{string} adds the item named {string} with a price of {double} and a preparation time of {int}:{int} to the group order")
    public void addsTheItemNamedWithAPriceOfAndAPreparationTimeOfToTheGroupOrder(String name, String menu, double price, int hours, int minutes) {
        steatsMap.get(name).addMenuItem(new MenuItem(menu, price, Duration.ofHours(hours).plusMinutes(minutes)));
    }

    @Then("{string} he need to choose the delivery time so he gets the next delivery time from {string} to {string} and gets :")
    public void whenToGetsTheNextDeliveryTimeFromTo(String name, String from, String to, List<Map<String, String>> deliveryTime) throws NotFoundException {
        assertEquals(
                deliveryTime.stream().map(item -> LocalDateTime.parse(item.get("deliveryTime"))).toList(),
                steatsMap.get(name).getAvailableDeliveryTimes(LocalDateTime.parse(from), LocalDateTime.parse(to))
        );
    }

    @And("{string} can choose the following delivery time : {string}")
    public void canChooseTheFollowingDeliveryTime(String name, String time) {
        assertDoesNotThrow(() -> steatsMap.get(name).changeDeliveryTime(LocalDateTime.parse(time)));
    }

    @When("{string} close the group order that doesn't have a delivery time")
    public void closeTheGroupOrderThatDoesnTHaveADeliveryTime(String name) {
        assertThrows(IllegalStateException.class, () -> steatsMap.get(name).closeGroupOrder());
    }
}
