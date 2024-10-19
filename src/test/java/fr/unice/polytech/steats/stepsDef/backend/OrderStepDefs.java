package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.*;
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

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

public class OrderStepDefs {

    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    Address address;
    List<Restaurant> restaurantListFiltered;


    @Before
    public void before() {
        RestaurantManager.getInstance().clear();
    }

    // Background for order test

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

    // Test for scenario : Creating an order

    @When("the user creates an order and specifies a date, an address and a restaurant")
    public void whenCreatesOrder() {
        deliveryTime = LocalDateTime.of(2024, 10, 16, 21, 0);
        address = new Address("ch de Carel", "Auribeau", "06810", "");
        stEats.createOrder(deliveryTime, address, restaurant);
    }

    @Then("the user can order")
    public void thenUserCanOrder() {
        assertFalse(stEats.getAvailableMenu().isEmpty());
    }

    // Test list order for scenario :

    @Then("The list of all restaurant displayed should contain the following restaurants:")
    public void theListOfAllRestaurantDisplayedShouldContainTheFollowingRestaurants(List<Map<String, String>> items) {
        for (Map<String, String> item : items) {
            assertTrue(restaurantListFiltered.stream()
                    .anyMatch(restaurantFiltered -> restaurantFiltered.getName().equals(item.get("name"))));
        }
        assertEquals(items.size(), restaurantListFiltered.size());
    }


    // Test for scenario : Filtering restaurants by name

    @When("The user filter by typing {string} and we have the following restaurants in the database:")
    public void theUserFilterByTypingAndWeHaveTheFollowingRestaurantsInTheDatabase(String nameSearched, List<Map<String, String>> items) {
        for (Map<String, String> item : items) {
            RestaurantManager.getInstance().add(item.get("name"), new Restaurant(item.get("name")));
        }
        restaurantListFiltered = RestaurantManager.filterRestaurantByName(nameSearched);
    }

    // Test for scenario : Filtering restaurants by type of food

    @When("The user select {string} and we have the following restaurants in the database:")
    public void theUserSelectAndWeHaveTheFollowingRestaurantsInTheDatabase(String typeOfFood, List<Map<String, String>> items) {
        for (Map<String, String> item : items) {
            RestaurantManager.getInstance().add(item.get("name"), new Restaurant(item.get("name"), TypeOfFood.valueOf(item.get("typeOfFood"))));
        }
        restaurantListFiltered = RestaurantManager.filterRestaurantByTypeOfFood(TypeOfFood.valueOf(typeOfFood));
    }

    // Test for scenario : Filtering restaurants by delivery time

    @When("The user choose to filter all restaurant that can deliver a MenuItem for {string} and we have the following restaurants in the database:")
    public void theUserChooseToFilterAllRestaurantThatCanDeliverAMenuItemForAndWeHaveTheFollowingRestaurantsInTheDatabase(String deliveryTime, List<Map<String, String>> items) throws NotFoundException {
        LocalDateTime deliveryTimeParsed = LocalDateTime.parse(deliveryTime);
        RestaurantManager.getInstance().remove("La Cafet");
        for (Map<String, String> item : items) {
            restaurant = new Restaurant(item.get("name"));
            DateTimeFormatter parser = DateTimeFormatter.ofPattern("H:mm:ss");
            LocalTime localTimeParsed = LocalTime.parse(item.get("scheduleStart"), parser);
            Schedule schedule = new Schedule(localTimeParsed, Duration.ofMinutes(30), 1, DayOfWeek.FRIDAY);
            restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
            restaurant.addSchedule(schedule);
            SingleOrder order = new SingleOrder("1", deliveryTimeParsed, new Address("ch de Carel", "Auribeau", "06810", ""), restaurant);
            Duration durationOrder = Duration.ofMinutes(Long.parseLong(item.get("orderDuration")));
            order.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, durationOrder));
            restaurant.addOrder(order);
            RestaurantManager.getInstance().add(item.get("name"), restaurant);
        }
        restaurantListFiltered = RestaurantManager.filterRestaurantByDeliveryTime(deliveryTimeParsed);
    }
}
