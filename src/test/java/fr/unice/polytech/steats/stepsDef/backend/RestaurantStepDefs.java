package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.restaurant.Restaurant;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RestaurantStepDefs {

    Restaurant restaurant;
    MenuItem menuItem;

    @Given("A restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
    }

    @Given("a menuItem named {string} with a price of {int}")
    public void givenARestaurantNamed(String menuItemName, Integer price) {
        menuItem = new MenuItem(menuItemName, price, Duration.ofMinutes(20));
    }

    @When("{string} add {string}")
    public void whenRestaurantAddMenuItem(String restaurantName, String menuItemName) {
        if (restaurant.getName().equals(restaurantName) && menuItem.getName().equals(menuItemName))
            restaurant.addMenuItem(menuItem);
    }

    @Then("{string} is added to the menu")
    public void thenTheMenuItemIsAddedToTheMenu(String menuItemName) {
        assertTrue(restaurant.getFullMenu().stream().anyMatch(item -> item.getName().equals(menuItemName)));
    }

    @When("{string} remove {string}")
    public void whenRestaurantRemoveMenuItem(String restaurantName, String menuItemName) {
        if (restaurant.getName().equals(restaurantName) && menuItem.getName().equals(menuItemName)) {
            restaurant.removeMenuItem(menuItem);
        }
    }

    @Then("{string} is removed from the menu")
    public void thenTheMenuItemIsRemovedFromTheMenu(String menuItemName) {
        assertFalse(restaurant.getFullMenu().stream().anyMatch(item -> item.getName().equals(menuItemName)));
    }
}
