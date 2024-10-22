package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.discounts.DiscountBuilder;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.restaurant.RestaurantManager;
import fr.unice.polytech.steats.restaurant.TypeOfFood;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountStepdefs {
    private String restaurant;
    private String username;
    private SingleOrder order;

    @Before
    public void before() {
        RestaurantManager.getInstance().clear();
        UserManager.getInstance().clear();
    }


    @Given("a restaurant named {string} of type {string}")
    public void givenARestaurant(String restaurantName, String foodType) {
        if (!RestaurantManager.getInstance().contains(restaurantName))
            RestaurantManager.getInstance().add(restaurantName, new Restaurant(restaurantName, TypeOfFood.valueOf(foodType)));
        restaurant = restaurantName;
    }

    @And("a discount of {double}% each {int} orders")
    public void aDiscountOfEachOrders(double percent, int orderAmount) throws NotFoundException {
        RestaurantManager.getInstance().get(restaurant).addDiscount(new DiscountBuilder()
                .setOrderDiscount(percent / 100.0)
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .build());
    }

    @And("a discount of {double}% if the client has the {string} role")
    public void aDiscountOfIfTheClientHasTheRole(double percent, String role) throws NotFoundException {
        RestaurantManager.getInstance().get(restaurant).addDiscount(new DiscountBuilder()
                .setOrderDiscount(percent / 100.0)
                .setUserRoles(Role.valueOf(role))
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @And("each {int} orders, an offer of the following free products:")
    public void eachOrdersAnOfferOfTheFollowingFreeProducts(int orderAmount, List<Map<String, String>> items) throws NotFoundException {
        RestaurantManager.getInstance().get(restaurant).addDiscount(new DiscountBuilder()
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

    @And("a discount of {double}€ the next time if the order has more than {int} items")
    public void aDiscountOfEuroTheNextTimeIfTheOrderHasMoreThanItems(double moneyAmount, int minItemAmount) throws NotFoundException {
        RestaurantManager.getInstance().get(restaurant).addDiscount(new DiscountBuilder()
                .setOrderCredit(moneyAmount)
                .setCurrentOrderItemsAmount(minItemAmount)
                .appliesAfterOrder()
                .unstackable()
                .build());
    }

    @And("a discount of {double}€ if the order has more than {int} items")
    public void aDiscountOfEuroIfTheOrderHasMoreThanItems(double moneyAmount, int minItemAmount) throws NotFoundException {
        RestaurantManager.getInstance().get(restaurant).addDiscount(new DiscountBuilder()
                .setOrderCredit(moneyAmount)
                .setCurrentOrderItemsAmount(minItemAmount)
                .appliesDuringOrder()
                .unstackable()
                .build());
    }

    @Given("I am {string} with the {string} role and {int} orders at {string} of {int} items")
    public void iAmAClientWithTheRole(String name, String role, int orders, String restaurant, int items) throws NotFoundException {
        aClientNamedWithTheRole(name, role);
        for (int i = 0; i < orders; i++) {
            SingleOrder singleOrder = new SingleOrder(username, null, null, restaurant);
            for (int j = 0; j < items; j++)
                singleOrder.addMenuItem(new MenuItem("P1", 5, Duration.ofMinutes(1)));
            UserManager.getInstance().get(username).addOrderToHistory(singleOrder);
        }
    }

    @Given("a client named {string} with the {string} role")
    public void aClientNamedWithTheRole(String name, String role) {
        username = name;
        UserManager.getInstance().add(name, new User(name, name, Role.valueOf(role)));
    }

    @When("I place an order at {string} with the following items:")
    public void iPlaceAnOrderWithTheFollowingItems(String restaurant, List<Map<String, String>> items) {
        order = new SingleOrder(username, null, null, restaurant);
        items.forEach(item -> order.addMenuItem(new MenuItem(item.get("name"), 5, Duration.ofMinutes(1))));
    }

    @Then("I should receive a {double}% discount")
    public void iShouldReceiveADiscount(double percent) {
        assertEquals(1 - percent / 100.0, order.getPrice() / order.getSubPrice(), 0.001);
    }

    @Then("I should receive a {double}€ discount")
    public void iShouldReceiveAEuroDiscount(double amount) {
        assertEquals(amount, order.getSubPrice() - order.getPrice());
    }

    @Then("My cart should contain the following items:")
    public void myCartShouldContainTheFollowingItems(List<Map<String, String>> items) {
        List<MenuItem> orderItems = order.getItems();
        assertAll(items.stream().map(item -> () -> assertTrue(orderItems.stream().anyMatch(orderItem -> orderItem.getName().equals(item.get("name"))))));
    }

    @Then("I shouldn't receive a discount")
    public void iShouldnTReceiveADiscount() {
        assertEquals(0, order.getDiscounts().size());
    }
}
