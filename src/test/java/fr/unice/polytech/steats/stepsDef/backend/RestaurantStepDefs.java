package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalTime;

import static org.junit.Assert.assertTrue;

public class RestaurantStepDefs {

    Restaurant restaurant = new Restaurant("Restaurant");

    public RestaurantStepDefs() {
    }

    @Given("A restaurant named {string}")
    public void givenARestaurant(String restaurantName) {
        // Instanced above
    }

    @When("{string} add a new menu item")
    public void whenRestaurantAddMenuItem(String restaurantName) {
        LocalTime preparationTime = LocalTime.of(0, 20, 0);
        MenuItem boeufBourguignon = new MenuItem("Boeuf Bourguignon", 25, preparationTime);
        restaurant.addMenuItem(boeufBourguignon);
    }

    @Then("the menu item is added to the menu")
    public void thenTheMenuItemIsAddedToTheMenu() {
        assertTrue(restaurant.getMenu().stream().anyMatch(menuItem -> menuItem.getName().equals("Boeuf Bourguignon")));
    }
}
