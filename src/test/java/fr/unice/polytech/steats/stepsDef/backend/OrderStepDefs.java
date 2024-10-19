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

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

public class OrderStepDefs {

    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    Address address;
    List<Restaurant> restaurantListFilteredName;
    List<Restaurant> restaurantListFilteredTypeOfFood;
    List<Restaurant> restaurantListFilteredDeliveryTime;


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


    // Test for scenario : Filtering restaurants by name

    @When("The user filter by typing {string}")
    public void theUserFilterByTyping(String restaurantName) {
        restaurantListFilteredName = RestaurantManager.filterRestaurantByName(restaurantName);
    }

    @Then("The list of all restaurant containing {string} are displayed")
    public void theListOfAllRestaurantContainingAreDisplayed(String restaurantName) {
        assertTrue(restaurantListFilteredName.stream().allMatch(restaurantFiltered -> restaurantFiltered.getName()
                .toLowerCase()
                .contains(restaurantName.toLowerCase())));
        assertEquals(2, restaurantListFilteredName.size());
    }

    @When("The user select {string} and thus filter by type of food")
    public void theUserSelectAndThusFilterByTypeOfFood(String typeOfFood) {
        restaurantListFilteredTypeOfFood = RestaurantManager.filterRestaurantByTypeOfFood(TypeOfFood.valueOf(typeOfFood));
    }

    @Then("The list of all restaurant of type {string} are displayed")
    public void theListOfAllRestaurantOfTypeAreDisplayed(String typeOfFood) {
        assertTrue(restaurantListFilteredTypeOfFood.stream()
                .allMatch(restaurantFiltered -> restaurantFiltered.getTypeOfFood() == TypeOfFood.valueOf(typeOfFood)));
        assertEquals(2, restaurantListFilteredTypeOfFood.size());
    }

    @Given("an available restaurant named {string} with schedule that starts at {string}")
    public void anAvailableRestaurantNamedWithScheduleThatStartsAt(String restaurantName, String localTime) {
        restaurant = new Restaurant(restaurantName);
        if (!RestaurantManager.getInstance().contains(restaurantName))
            RestaurantManager.getInstance().add(restaurantName, restaurant);
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("H:mm:ss");
        LocalTime localTimeParsed = LocalTime.parse(localTime, parser);
        Schedule schedule = new Schedule(localTimeParsed, Duration.ofMinutes(30), 1, DayOfWeek.FRIDAY);
        restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
        restaurant.addSchedule(schedule);
    }

    @Given("a fully booked restaurant named {string} with schedule that starts at {string}")
    public void aFullyBookedRestaurantNamedWithScheduleThatStartsAt(String restaurantName, String localTime) {
        restaurant = new Restaurant(restaurantName);
        if (!RestaurantManager.getInstance().contains(restaurantName))
            RestaurantManager.getInstance().add(restaurantName, restaurant);
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("H:mm:ss");
        LocalTime localTimeParsed = LocalTime.parse(localTime, parser);
        Schedule schedule = new Schedule(localTimeParsed, Duration.ofMinutes(30), 1, DayOfWeek.FRIDAY);
        restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
        restaurant.addSchedule(schedule);
        SingleOrder order = new SingleOrder("1", LocalDateTime.of(2024, 3, 29, 10, 0), new Address("ch de Carel", "Auribeau", "06810", ""), restaurant);
        order.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
        restaurant.addOrder(order);
    }

    @When("The user choose to filter all restaurant that can deliver a MenuItem for {string}")
    public void theUserChooseToFilterAllRestaurantThatCanDeliverAMenuItemFor(String localDateTime) throws NotFoundException {
        RestaurantManager.getInstance().remove("La Cafet");
        LocalDateTime deliveryTime = LocalDateTime.parse(localDateTime);
        restaurantListFilteredDeliveryTime = RestaurantManager.filterRestaurantByDeliveryTime(deliveryTime);
    }

    @Then("The list of all restaurant that can deliver at least one MenuItem for {string} are displayed")
    public void theListOfAllRestaurantThatCanDeliverAtLeastOneMenuItemForAreDisplayed(String localDateTime) {
        LocalDateTime deliveryTime = LocalDateTime.parse(localDateTime);
        assertTrue(restaurantListFilteredDeliveryTime.stream()
                .allMatch(restaurantFiltered -> restaurantFiltered.canDeliverAt(deliveryTime)));
        assertEquals(3, restaurantListFilteredDeliveryTime.size());
    }
}
