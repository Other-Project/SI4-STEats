package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalTime;

import static org.junit.Assert.assertTrue;

public class RestaurantStepDefs {

    Restaurant restaurant;
    MenuItem menuItem;

    public RestaurantStepDefs() {
    }

    @Given("A restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        restaurant = new Restaurant(restaurantName);
    }

    @Given("a menuItem named {string} with a price of {int}")
    public void givenARestaurantNamed(String menuItemName, Integer price) {
        menuItem = new MenuItem(menuItemName, price, LocalTime.of(0, 20, 0));
    }

    @When("{string} add {string}")
    public void whenRestaurantAddMenuItem(String restaurantName, String menuItemName) {
        if (restaurant.name().equals(restaurantName) && menuItem.getName().equals(menuItemName)) {
            restaurant.addMenuItem(menuItem);
        }
    }

    @Then("{string} is added to the menu")
    public void thenTheMenuItemIsAddedToTheMenu(String menuItemName) {
        assertTrue(restaurant.getMenu().stream().anyMatch(menuItem -> menuItem.getName().equals(menuItemName)));
    }
}
