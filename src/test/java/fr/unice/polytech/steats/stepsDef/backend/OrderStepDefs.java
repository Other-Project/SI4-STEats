package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderStepDefs {

    User user;
    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    Address address;
    Exception exception;

    @Given("an user of name {string}")
    public void givenAnUser(String userName) {
        steatsController = new STEatsController();
        user = STEatsController.USER_REGISTRY.findByName(userName).isPresent()
                ? STEatsController.USER_REGISTRY.findByName(userName).get() : null;
        assertNotNull(user);
        assertDoesNotThrow(() -> {
            stEats = steatsController.logging(user.getName());
        });
    }

    @Given("a restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
        restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, LocalTime.of(0, 20, 0)));
        restaurant.addMenuItem(new MenuItem("Pavé de saumon", 25, LocalTime.of(0, 20, 0)));
    }

    @When("the user creates an order and specifies a date, an address and a restaurant")
    public void whenCreatesOrder() {
        deliveryTime = LocalDateTime.now();
        address = new Address("ch de Carel", "Auribeau", "06810", "");
        stEats.createOrder(deliveryTime, address, restaurant);
    }

    @Then("the user can order")
    public void thenUserCanOrder() {
        assertFalse(stEats.getAvailableMenu().isEmpty());
    }
}
