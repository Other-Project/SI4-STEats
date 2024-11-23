package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.models.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiscountStepdefs {
    private String restaurant;
    private String username;
    private SingleOrder order;

    @Before
    public void before() {
        RestaurantManager.getInstance().clear();
        UserManager.getInstance().clear();
        SingleOrderManager.getInstance().clear();
        AddressManager.getInstance().clear();
        AddressManager.getInstance().add("Campus SophiaTech", new Address("Campus SophiaTech", "930 Rt des Colles", "Biot", "06410", ""));

        RestaurantDiscountManager.getInstance().clear();
    }

    @Given("a restaurant named {string} of type {string}")
    public void givenARestaurant(String restaurantName, String foodType) {
        if (!RestaurantManager.getInstance().contains(restaurantName))
            RestaurantManager.getInstance().add(restaurantName, new Restaurant(restaurantName, restaurantName, TypeOfFood.valueOf(foodType), Duration.ofMinutes(30)));
        restaurant = restaurantName;
    }

    @And("a discount of {double}% each {int} orders")
    public void aDiscountOfEachOrders(double percent, int orderAmount) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant)
                .setOrderDiscount(percent / 100.0)
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .build());
    }

    @And("a discount of {double}% if the client has the {string} role")
    public void aDiscountOfIfTheClientHasTheRole(double percent, String role) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant)
                .setOrderDiscount(percent / 100.0)
                .setUserRoles(Role.valueOf(role))
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @And("each {int} orders, an offer of the following free products:")
    public void eachOrdersAnOfferOfTheFollowingFreeProducts(int orderAmount, List<Map<String, String>> items) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant)
                .setFreeItems(items.stream().map(item -> item.get("name")).toArray(String[]::new))
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @And("a discount of {double}€ the next time if the order has more than {int} items")
    public void aDiscountOfEuroTheNextTimeIfTheOrderHasMoreThanItems(double moneyAmount, int minItemAmount) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant)
                .setOrderCredit(moneyAmount)
                .setCurrentOrderItemsAmount(minItemAmount)
                .appliesAfterOrder()
                .unstackable()
                .build());
    }

    @And("a discount of {double}€ if the order has more than {int} items")
    public void aDiscountOfEuroIfTheOrderHasMoreThanItems(double moneyAmount, int minItemAmount) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant)
                .setOrderCredit(moneyAmount)
                .setCurrentOrderItemsAmount(minItemAmount)
                .appliesDuringOrder()
                .unstackable()
                .build());
    }

    @Given("I am {string} with the {string} role and {int} orders at {string} of {int} items")
    public void iAmAClientWithTheRole(String name, String role, int orders, String restaurant, int items) throws IOException {
        aClientNamedWithTheRole(name, role);
        for (int i = 0; i < orders; i++) {
            SingleOrder singleOrder = new SingleOrder(username, null, "Campus SophiaTech", restaurant);
            for (int j = 0; j < items; j++)
                singleOrder.addMenuItem(new MenuItem("P1", "P1", 5, Duration.ofMinutes(1), restaurant));
            assertTrue(singleOrder.pay());
        }
    }

    @Given("a client named {string} with the {string} role")
    public void aClientNamedWithTheRole(String name, String role) {
        username = name;
        UserManager.getInstance().add(name, new User(name, name, Role.valueOf(role)));
    }

    @When("I place an order at {string} with the following items:")
    public void iPlaceAnOrderWithTheFollowingItems(String restaurant, List<Map<String, String>> items) {
        order = new SingleOrder(username, null, "Campus SophiaTech", restaurant);
        items.forEach(item -> order.addMenuItem(new MenuItem(item.get("name"), item.get("name"), 5, Duration.ofMinutes(1), restaurant)));
    }

    @Then("I should receive a {double}% discount")
    public void iShouldReceiveADiscount(double percent) {
        assertEquals(1 - percent / 100.0, order.price() / order.getSubPrice(), 0.001);
    }

    @Then("I should receive a {double}€ discount")
    public void iShouldReceiveAEuroDiscount(double amount) {
        assertEquals(amount, order.getSubPrice() - order.price());
    }

    @Then("My cart should contain the following items:")
    public void myCartShouldContainTheFollowingItems(List<Map<String, String>> items) {
        List<String> orderItems = order.items();
        assertAll(items.stream().map(item -> () -> assertTrue(orderItems.stream().anyMatch(orderItem -> orderItem.equals(item.get("name"))))));
    }

    @Then("I shouldn't receive a discount")
    public void iShouldnTReceiveADiscount() {
        assertEquals(0, order.getDiscounts().size());
    }
}
