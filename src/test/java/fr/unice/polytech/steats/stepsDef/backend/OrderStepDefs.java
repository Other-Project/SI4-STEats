package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.AddressManager;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.*;
import fr.unice.polytech.steats.order.Status;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.Schedule;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.UserManager;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderStepDefs {
    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    List<Restaurant> restaurantListFiltered;

    @Before
    public void before() {
        RestaurantManager.getInstance().clear();
        UserManager.getInstance().clear();
    }

    //region Background for order test

    @Given("an user of id {string}")
    public void givenAnUser(String userId) {
        steatsController = new STEatsController();
        UserManager.getInstance().fillForDemo();
        assertDoesNotThrow(() -> stEats = steatsController.logging(userId));
    }

    @Given("a restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
        if (!RestaurantManager.getInstance().contains(restaurantName))
            RestaurantManager.getInstance().add(restaurantName, restaurant);
        Schedule schedule = new Schedule(LocalTime.of(20, 15), Duration.ofMinutes(30), 5, DayOfWeek.WEDNESDAY);
        restaurant.addSchedule(schedule);
        restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
        restaurant.addMenuItem(new MenuItem("Pavé de saumon", 25, Duration.ofMinutes(20)));
    }

    //endregion

    //region Test for scenario : Creating an order

    @When("the user creates an order and specifies a date, an address and a restaurant")
    public void whenCreatesOrder() {
        deliveryTime = LocalDateTime.of(2024, 10, 16, 21, 0);
        stEats.createOrder(deliveryTime, null, restaurant);
    }

    @Then("the user can order")
    public void thenUserCanOrder() {
        assertFalse(stEats.getAvailableMenu().isEmpty());
    }

    //endregion

    //region Test list order for scenario :

    @Then("The list of all restaurant displayed should contain the following restaurants:")
    public void theListOfAllRestaurantDisplayedShouldContainTheFollowingRestaurants(List<Map<String, String>> items) {
        for (Map<String, String> item : items) {
            assertTrue(restaurantListFiltered.stream().anyMatch(restaurantFiltered -> restaurantFiltered.getName().equals(item.get("name"))));
        }
        assertEquals(items.size(), restaurantListFiltered.size());
    }

    //endregion

    //region Test for scenario : Filtering restaurants by name

    @Given("The following restaurants :")
    public void theFollowingRestaurants(List<Map<String, String>> items) {
        for (Map<String, String> item : items) {
            RestaurantManager.getInstance().add(item.get("name"), new Restaurant(item.get("name")));
        }
    }

    @When("The user filter by typing {string}")
    public void theUserFilterByTyping(String restaurantName) {
        restaurantListFiltered = RestaurantManager.filterRestaurant(restaurantName);
    }

    //endregion

    //region Test for scenario : Filtering restaurants by type of food

    @Given("The following restaurants with type of food :")
    public void theFollowingRestaurantsWithTypeOfFood(List<Map<String, String>> items) {
        for (Map<String, String> item : items) {
            RestaurantManager.getInstance().add(item.get("name"), new Restaurant(item.get("name"), TypeOfFood.valueOf(item.get("typeOfFood"))));
        }
    }

    @When("The user filter by selecting {string}")
    public void theUserFilterBySelecting(String typeOfFood) {
        restaurantListFiltered = RestaurantManager.filterRestaurant(TypeOfFood.valueOf(typeOfFood));
    }

    //endregion

    //region Test for scenario : Filtering restaurants by delivery time

    @Given("The following restaurants with schedule and order duration and order scheduled to {string} :")
    public void theFollowingRestaurantsWithScheduleAndOrderDurationAndOrderScheduledTo(String deliveryTime, List<Map<String, String>> items) {
        LocalDateTime deliveryTimeParsed = LocalDateTime.parse(deliveryTime);
        for (Map<String, String> item : items) {
            restaurant = new Restaurant(item.get("name"));
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("H:mm:ss");
            LocalTime localTimeParsed = LocalTime.parse(item.get("scheduleStart"), parser);
            Schedule schedule = new Schedule(localTimeParsed, Duration.ofMinutes(30), 1, DayOfWeek.FRIDAY);
            restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
            restaurant.addSchedule(schedule);
            SingleOrder order = new SingleOrder("1", deliveryTimeParsed, "Campus Sophia Tech", restaurant);
            Duration durationOrder = Duration.ofMinutes(Long.parseLong(item.get("preparationTime")));
            order.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, durationOrder));
            restaurant.addOrder(order);
            RestaurantManager.getInstance().add(item.get("name"), restaurant);
        }
    }

    @When("The user filter by selecting a delivery time of {string}")
    public void theUserFilterBySelectingADeliveryTimeOf(String deliveryTime) {
        LocalDateTime deliveryTimeParsed = LocalDateTime.parse(deliveryTime);
        restaurantListFiltered = RestaurantManager.filterRestaurant(deliveryTimeParsed);
    }

    //endregion

    //region Test for scenario : Filtering restaurants by name and type of food

    @When("The user filter by typing {string} and selecting {string}")
    public void theUserFilterByTypingAndSelecting(String restaurantName, String typeOfFood) {
        restaurantListFiltered = RestaurantManager.filterRestaurant(restaurantName, TypeOfFood.valueOf(typeOfFood), null);
    }

    @Given("The address labelled {string}")
    public void theAddressLabelled(String addressLabel) {
        if (!AddressManager.getInstance().contains(addressLabel))
            AddressManager.getInstance().add(addressLabel, new Address("Campus Sophia Tech", "1", "rue", "ville", "codePostal"));
    }

    //endregion

    @Given("an order to be delivered at {string}")
    public void givenTheOrderTheUserCreated(String addressId) {
        stEats.createOrder(deliveryTime, addressId, restaurant);
    }

    @When("the user orders the following items from the given restaurant:")
    public void whenSelectsMenuItemsFromRestaurant(List<Map<String, String>> items) {
        items.forEach(item -> stEats.addMenuItem(
                stEats.getAvailableMenu().stream()
                        .filter(menuItem -> menuItem.getName().equals(item.get("menuItems")))
                        .findFirst().orElseThrow()
        ));
    }

    @Then("the following items are in his cart:")
    public void thenItemsAreAddedToHisCart(List<Map<String, String>> items) {
        List<String> cart = stEats.getCart().stream().map(MenuItem::getName).toList();
        assertEquals(items.size(), cart.size());
        items.forEach(item -> assertTrue(cart.contains(item.get("menuItems"))));
    }

    @When("the user deletes {string}")
    public void whenDeletesOrderedBefore(String itemName) {
        stEats.removeMenuItem(stEats.getCart().stream().filter(item -> item.getName().equals(itemName)).findFirst().orElseThrow());
    }

    @Then("{string} doesn't appear in the cart anymore")
    public void thenDoesntAppearTheCart(String itemName) {
        assertFalse(stEats.getCart().stream().anyMatch(item -> item.getName().equals(itemName)));
    }

    @When("the user pays for the items in its cart")
    public void whenWantsToPayTheOrder() throws NotFoundException {
        assertTrue(stEats.payOrder());
    }

    @Then("the order has the {string} status")
    public void thenUserPaysTheOrderAndTheOrderIsClosed(String status) {
        assertEquals(stEats.getOrder().getStatus(), Status.valueOf(status));
    }
}
