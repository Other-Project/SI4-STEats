package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.helpers.MenuItemServiceHelper;
import fr.unice.polytech.steats.models.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountStepdefs {
    private Restaurant restaurant;
    private User user;
    private SingleOrder order;
    private final List<SingleOrder> orderHistory = new ArrayList<>();
    private final Map<String, MenuItem> freeItems = new HashMap<>();
    MockedStatic<MenuItemServiceHelper> menuItemServiceHelperMocked;

    @Before
    public void before() {
        orderHistory.clear();
        freeItems.clear();
        RestaurantDiscountManager.getInstance().clear();
        menuItemServiceHelperMocked = Mockito.mockStatic(MenuItemServiceHelper.class);
        menuItemServiceHelperMocked.when(() -> MenuItemServiceHelper.getMenuItem(Mockito.anyString())).thenAnswer(invocation -> freeItems.get(invocation.<String>getArgument(0)));
    }

    @After
    public void terminate() {
        menuItemServiceHelperMocked.close();
    }

    @Given("a restaurant named {string} of type {string}")
    public void givenARestaurant(String restaurantName, String foodType) {
        restaurant = new Restaurant(restaurantName, restaurantName, TypeOfFood.valueOf(foodType), Duration.ofMinutes(30));
    }

    @And("a discount of {double}% each {int} orders")
    public void aDiscountOfEachOrders(double percent, int orderAmount) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant.id())
                .setOrderDiscount(percent / 100.0)
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .build());
    }

    @And("a discount of {double}% if the client has the {string} role")
    public void aDiscountOfIfTheClientHasTheRole(double percent, String role) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant.id())
                .setOrderDiscount(percent / 100.0)
                .setUserRoles(Role.valueOf(role))
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @And("each {int} orders, an offer of the following free products:")
    public void eachOrdersAnOfferOfTheFollowingFreeProducts(int orderAmount, List<Map<String, String>> items) {
        freeItems.putAll(items.stream().map(item -> new MenuItem(item.get("name"), item.get("name"), 0, Duration.ofSeconds(15), restaurant.id())).collect(Collectors.toMap(MenuItem::id, item -> item)));
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant.id())
                .setFreeItems(items.stream().map(item -> item.get("name")).toArray(String[]::new))
                .setOrdersAmount(orderAmount)
                .appliesDuringOrder()
                .stackable()
                .build());
    }

    @And("a discount of {double}€ the next time if the order has more than {int} items")
    public void aDiscountOfEuroTheNextTimeIfTheOrderHasMoreThanItems(double moneyAmount, int minItemAmount) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant.id())
                .setOrderCredit(moneyAmount)
                .setCurrentOrderItemsAmount(minItemAmount)
                .appliesAfterOrder()
                .unstackable()
                .build());
    }

    @And("a discount of {double}€ if the order has more than {int} items")
    public void aDiscountOfEuroIfTheOrderHasMoreThanItems(double moneyAmount, int minItemAmount) {
        RestaurantDiscountManager.getInstance().add(new DiscountBuilder(restaurant.id())
                .setOrderCredit(moneyAmount)
                .setCurrentOrderItemsAmount(minItemAmount)
                .appliesDuringOrder()
                .unstackable()
                .build());
    }

    @Given("I am {string} with the {string} role and {int} orders at {string} of {int} items")
    public void iAmAClientWithTheRole(String name, String role, int orders, String restaurant, int items) {
        aClientNamedWithTheRole(name, role);
        for (int i = 0; i < orders; i++)
            orderHistory.add(new SingleOrder("order" + i, user.userId(), null, LocalDateTime.now().plusHours(2), "Campus SophiaTech", restaurant, Status.DELIVERED, Map.of("P1", items), Map.of("P1", items), List.of(), Duration.ZERO, LocalDateTime.now(), 5, 5));
    }

    @Given("a client named {string} with the {string} role")
    public void aClientNamedWithTheRole(String name, String role) {
        user = new User(name, name, Role.valueOf(role));
    }

    @When("I place an order at {string} with the following items:")
    public void iPlaceAnOrderWithTheFollowingItems(String restaurant, List<Map<String, String>> items) {
        Map<String, Integer> cart = items.stream().collect(Collectors.toMap(itemId -> itemId.get("name"), itemId -> itemId.containsKey("quantity") ? Integer.parseInt(itemId.get("quantity")) : 1, Integer::sum));
        order = new SingleOrder("orderId", user.userId(), null, LocalDateTime.now().plusHours(2), "Campus SophiaTech", restaurant, Status.INITIALISED, cart, cart, List.of(), Duration.ZERO, LocalDateTime.now(), 5, 5);
    }

    @Then("I should receive a {double}% discount")
    public void iShouldReceiveADiscount(double percent) {
        assertEquals(1 - percent / 100.0, price() / order.subPrice(), 0.001);
    }

    @Then("I should receive a {double}€ discount")
    public void iShouldReceiveAEuroDiscount(double amount) {
        assertEquals(amount, order.subPrice() - price());
    }

    @Then("My cart should contain the following items:")
    public void myCartShouldContainTheFollowingItems(List<Map<String, String>> items) {
        Map<String, Integer> orderItems = new HashMap<>(order.orderedItems());
        orderItems.putAll(assertDoesNotThrow(() -> RestaurantDiscountManager.getInstance().getApplicableDiscounts(order, user, orderHistory)).stream()
                .filter(discount -> !discount.options().appliesAfterOrder())
                .flatMap(discount -> Arrays.stream(discount.effects().freeItemIds()))
                .collect(Collectors.toMap(itemId -> itemId, itemId -> 1, Integer::sum)));
        assertAll(items.stream().map(item -> () -> assertTrue(orderItems.keySet().stream().anyMatch(orderItem -> orderItem.equals(item.get("name"))), "Item " + item.get("name") + " not found in cart")));
    }

    @Then("I shouldn't receive a discount")
    public void iShouldnTReceiveADiscount() {
        assertEquals(0, assertDoesNotThrow(() -> RestaurantDiscountManager.getInstance().getApplicableDiscounts(order, user, orderHistory)).size());
    }

    private double price() {
        return assertDoesNotThrow(() -> RestaurantDiscountManager.getInstance().getApplicableDiscounts(order, user, orderHistory)).stream()
                .filter(discount -> !discount.options().appliesAfterOrder())
                .map(discount -> new RestaurantDiscount(discount.id(), discount.restaurantId(), discount.options(), discount.criteria(), discount.effects()))
                .reduce(order.subPrice(), (p, d) -> d.getNewPrice(p), Double::sum);
    }
}
