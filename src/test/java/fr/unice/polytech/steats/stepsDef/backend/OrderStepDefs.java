package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.Schedule;
import fr.unice.polytech.steats.user.UserManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderStepDefs {

    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;

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
}
