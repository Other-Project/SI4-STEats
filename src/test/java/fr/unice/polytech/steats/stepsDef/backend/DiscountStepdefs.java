package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.discounts.DiscountBuilder;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.TypeOfFood;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountStepdefs {
    private final Map<String, Restaurant> restaurants = new HashMap<>();
    private String restaurant;
    private User user;
    private SingleOrder order;

    @Given("a restaurant named {string} of type {string}")
    public void givenARestaurant(String restaurantName, String foodType) {
        restaurants.put(restaurantName, new Restaurant(restaurantName, TypeOfFood.valueOf(foodType)));
        restaurant = restaurantName;
    }

    @And("a discount of {int}% each {int} orders")
    public void aDiscountOfEachOrders(int percent, int orderAmount) {
        restaurants.get(restaurant).addDiscount(new DiscountBuilder()
                .setOrderDiscount(percent / 100.0)
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .build());
    }

    @And("a discount of {int}% if the client has the {string} role")
    public void aDiscountOfIfTheClientHasTheRole(int percent, String role) {
        restaurants.get(restaurant).addDiscount(new DiscountBuilder()
                .setOrderDiscount(percent / 100.0)
                .setUserRoles(Role.valueOf(role))
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @And("each {int} orders, an offer of the following free products:")
    public void eachOrdersAnOfferOfTheFolowingFreeProducts(int orderAmount, List<Map<String, String>> items) {
        restaurants.get(restaurant).addDiscount(new DiscountBuilder()
                .setFreeItems(items.stream().map(item -> new MenuItem(
                        item.get("name"),
                        0,
                        Duration.ofSeconds(15)
                )).toArray(MenuItem[]::new))
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @Given("I am a client with the {string} role and {int} orders at {string}")
    public void iAmAnExternalClientWithOrders(String role, int orders, String restaurant) {
        user = new User(role, role, Role.valueOf(role));
        for (int i = 0; i < orders; i++)
            user.addOrderToHistory(new SingleOrder(user, null, null, restaurants.get(restaurant)));
    }

    @When("I place an order at {string} with the following items:")
    public void iPlaceAnOrderWithTheFollowingItems(String restaurant, List<Map<String, String>> items) {
        order = new SingleOrder(user, null, null, restaurants.get(restaurant));
        items.forEach(item -> order.addMenuItem(new MenuItem(item.get("name"), 5, Duration.ofMinutes(1))));
    }

    @Then("I should receive a {int}% discount")
    public void iShouldReceiveADiscount(int percent) {
        assertEquals(1 - percent / 100.0, order.getPrice() / order.getSubPrice());
    }

    @Then("My cart should contain the following items:")
    public void myCartShouldContainTheFollowingItems(List<Map<String, String>> items) {
        List<MenuItem> orderItems = order.getItems();
        assertAll(items.stream().map(item -> () -> assertTrue(orderItems.stream().anyMatch(orderItem -> orderItem.getName().equals(item.get("name"))))));
    }
}
