package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;

import static org.junit.Assert.assertFalse;

public class OrderStepDefs {

    User user;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    Address address;
    STEats stEats;

    public OrderStepDefs() {
    }

    @Given("an user of name {string} and with userId {string}")
    public void givenAnUser(String userName, String userId) {
        user = new User(userName, userId, Role.STUDENT);
        stEats = new STEats(user); // Create the link between the user and the app
    }

    @Given("a restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
    }

    @When("{string} creates an order and specifies a date, an address and a restaurant")
    public void whenCreatesOrder(String userName) {
        deliveryTime = LocalDateTime.now();
        address = new Address("ch de Carel", "Auribeau", "06810", "");
        stEats.createOrder(deliveryTime, address, restaurant);
    }

    @Then("A new order is created")
    public void thenOrderIsCreated() {
        assertFalse(restaurant.getOrders().isEmpty());
    }
}
