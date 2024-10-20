package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.UserManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Before;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderStepDefs {

    STEats stEats;
    STEatsController steatsController;
    Restaurant restaurant;
    LocalDateTime deliveryTime;
    Address address;

    @Before
    public void before() {
        UserManager.getInstance().clear();
    }

    @Given("an user of id {string}")
    public void givenAnUser(String userId) throws NotFoundException {
        steatsController = new STEatsController();
        UserManager.getInstance().fillForDemo();
        assertDoesNotThrow(() -> stEats = steatsController.logging(userId));
    }

    @Given("a restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
        restaurant.addMenuItem(new MenuItem("Boeuf Bourguignon", 25, Duration.ofMinutes(20)));
        restaurant.addMenuItem(new MenuItem("Pavé de saumon", 25, Duration.ofMinutes(20)));
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

    @Given("the order the user created")
    public void givenTheOrderTheUserCreated() {
        stEats.createOrder(deliveryTime, address, restaurant);
    }

    @When("the user orders {string} and {string} from the given restaurant")
    public void whenSelectsMenuItemsFromRestaurant(String menuItemName1, String menuItemName2) {
        stEats.addMenuItem(stEats.getAvailableMenu().stream()
                .filter(menuItem -> menuItem.getName().equals(menuItemName1))
                .findFirst().orElseThrow());
        stEats.addMenuItem(stEats.getAvailableMenu().stream()
                .filter(menuItem -> menuItem.getName().equals(menuItemName2))
                .findFirst().orElseThrow());
    }

    @Then("the items are added to his cart")
    public void thenItemsAreAddedToHisCart() {
        assertEquals(2, stEats.getCart().size());
        assertEquals("Boeuf Bourguignon", stEats.getCart().get(0).getName());
        assertEquals("Pavé de saumon", stEats.getCart().get(1).getName());

    }

    @When("the user deletes {string}")
    public void whenDeletesOrderedBefore(String itemName) {
        stEats.removeMenuItem(stEats.getCart().stream().filter(item -> item.getName().equals(itemName)).findFirst().orElseThrow());
    }

    @Then("{string} doesn't appear in the cart anymore")
    public void thenDoesntAppearTheCart(String itemName) {
        assertEquals(stEats.getCart().size(), 1);
        assertFalse(stEats.getCart().stream().anyMatch(item -> item.getName().equals(itemName)));
    }
}
