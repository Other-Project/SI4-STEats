package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserNotFoundException;
import fr.unice.polytech.steats.user.UserRegistry;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertFalse;

public class OrderStepDefs {

    UserRegistry userRegistry;
    User user;
    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    Address address;
    Exception exception;

    @Given("an user of name {string}")
    public void givenAnUser(String userName) throws UserNotFoundException {
        userRegistry = new UserRegistry();
        user = userRegistry.findByName(userName).isPresent() ? userRegistry.findByName(userName).get() : null;
        assert (user != null);
        steatsController = new STEatsController(userRegistry);
        try {
            stEats = steatsController.logging(user);
        } catch (UserNotFoundException e) {
            exception = e;
        }
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
