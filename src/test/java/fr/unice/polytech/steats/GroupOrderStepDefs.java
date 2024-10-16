package fr.unice.polytech.steats;

import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.GroupOrder;
import fr.unice.polytech.steats.order.GroupOrderManager;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.LocalDateTime;

public class GroupOrderStepDefs {

    private STEats steats;

    @Given("A group order with the group code {string} from the restaurant {string} and to deliver for {string} at {string}")
    public void a_group_order_is_created(String groupCode, String restaurant, String deliveryTime, String address) {
        GroupOrderManager.getInstance().add(groupCode, new GroupOrder(groupCode, LocalDateTime.parse(deliveryTime), new Address(address, "1", "1", "1"), new Restaurant(restaurant)));
    }

    @Given("The user named {string} with the id {string} is logged in")
    public void theUserWithTheIdIsLoggedIn(String name, String userId) {
        User user = new User(name, userId, Role.STUDENT);
        steats = new STEats(user);
    }

    @When("The user joins the group order with the group code {string}")
    public void the_user_joins_the_group_order(String groupCode) {
        steats.joinGroupOrder(groupCode);
    }

    @Then("The user with the id {string} is added to the group order with the group code {string}")
    public void the_user_is_added_to_the_group_order(String userId, String groupCode) {
        assert GroupOrderManager.getInstance().get(groupCode).getOrders().size() == 1;
        assert GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .filter(order -> order instanceof SingleOrder)
                .map(order -> ((SingleOrder) order).getUser().getUserId())
                .toList()
                .contains(userId);
    }

    @When("The user adds the item named {string} with a price of {double} to the group order")
    public void theUserWithTheIdAddsTheItemNamedWithAPriceOfToTheGroupOrderWithTheGroupCode(String menuItem, double price) {
        steats.addMenuItem(new MenuItem(menuItem, price, Duration.ofMinutes(10)));
    }

    @Then("The item with named {string} with a price of {double} is added to the order of the user with the id {string} in the group order with the group code {string}")
    public void theItemWithNamedIsAddedToTheOrderOfTheUserWithTheIdInTheGroupOrderWithTheGroupCode(String menuItem, Double price, String userId, String groupCode) {
        assert steats.getTotalPrice() == price;
        assert GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> order.getItems().size())
                .toList()
                .contains(1);
        assert GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> order.getItems().getFirst().getName())
                .toList()
                .contains(menuItem);
        assert GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> ((SingleOrder) order).getUser().getUserId())
                .toList().contains(userId);
    }

}
