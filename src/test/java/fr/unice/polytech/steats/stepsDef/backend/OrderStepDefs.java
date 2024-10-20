package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.Status;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.Schedule;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.UserManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Before;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderStepDefs {

    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;

    @Before
    public void before() {
        UserManager.getInstance().clear();
    }

    @Given("an user of id {string}")
    public void givenAnUser(String userId) {
        steatsController = new STEatsController();
        UserManager.getInstance().fillForDemo();
        assertDoesNotThrow(() -> stEats = steatsController.logging(userId));
    }

    @Given("a restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
        Schedule schedule = new Schedule(LocalTime.of(20, 15), Duration.ofMinutes(30), 5, DayOfWeek.WEDNESDAY);
        restaurant.addSchedule(schedule);
        restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
        restaurant.addMenuItem(new MenuItem("Pavé de saumon", 25, Duration.ofMinutes(20)));
    }

    @When("the user creates an order and specifies a date, an address and a restaurant")
    public void whenCreatesOrder() {
        deliveryTime = LocalDateTime.of(2024, 10, 16, 21, 0);
        stEats.createOrder(deliveryTime, null, restaurant);
    }

    @Then("the user can order")
    public void thenUserCanOrder() {
        assertFalse(stEats.getAvailableMenu().isEmpty());
    }

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

    @Then("the items are added to his cart")
    public void thenItemsAreAddedToHisCart() {
        List<String> cart = stEats.getCart().stream().map(MenuItem::getName).toList();
        assertEquals(2, cart.size());
        assertTrue(cart.contains("Boeuf Bourguignon"));
        assertTrue(cart.contains("Pavé de saumon"));

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

    @Then("the user pays the order and the order is closed")
    public void thenUserPaysTheOrderAndTheOrderIsClosed() throws NotFoundException {
        assertEquals(stEats.getOrder().getStatus(), Status.PAID);
    }
}
