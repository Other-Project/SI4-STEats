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

import java.time.LocalDateTime;
import java.time.LocalTime;

public class GroupOrderStepDefs {

    private User user;
    private STEats steats;

    @Given("A group order is created with the group code {string} and the delivery time {string} and the address {string} and the restaurant {string}")
    public void a_group_order_is_created(String groupCode, String deliveryTime, String address, String restaurant) {
        GroupOrderManager.addGroupOrder(new GroupOrder(groupCode, LocalDateTime.parse(deliveryTime), new Address(address, "1", "1", "1"), new Restaurant(restaurant)));
    }

    @Given("The user with the id {string} is logged in")
    public void theUserWithTheIdIsLoggedIn(String userId) {
        user = new User("Alex", userId, Role.STUDENT);
        steats = new STEats(user);
    }

    @When("The user with the id {string} joins the group order with the group code {string}")
    public void the_user_joins_the_group_order(String userId, String groupCode) {
        steats.joinGroupOrder(groupCode);
    }

    @Then("The user with the id {string} is added to the group order with the group code {string}")
    public void the_user_is_added_to_the_group_order(String userId, String groupCode) {
        assert GroupOrderManager.getGroupOrder(groupCode).getOrders().size() == 1;
        assert GroupOrderManager.getGroupOrder(groupCode).getOrders().stream()
                .filter(order -> order instanceof SingleOrder)
                .map(order -> ((SingleOrder) order).getUser().getUserId())
                .toList()
                .contains(userId);
    }

    @When("The user with the id {string} adds the item named {string} with a price of {double} to the group order with the group code {string}")
    public void theUserWithTheIdAddsTheItemNamedWithAPriceOfToTheGroupOrderWithTheGroupCode(String id, String menuItem, double price, String arg4) {
        steats.addMenuItem(new MenuItem(menuItem, price, LocalTime.of(12, 0)));
    }

    @Then("The item with named {string} is added to the order of the user with the id {string} in the group order with the group code {string}")
    public void theItemWithNamedIsAddedToTheOrderOfTheUserWithTheIdInTheGroupOrderWithTheGroupCode(String arg0, String arg1, String arg2) {
        assert steats.getTotalPrice() == 10.0;
        assert GroupOrderManager.getGroupOrder(arg2).getOrders().stream()
                .map(order -> order.getItems().size())
                .toList()
                .contains(1);
        assert GroupOrderManager.getGroupOrder(arg2).getOrders().stream()
                .map(order -> order.getItems().getFirst().getName())
                .toList()
                .contains(arg0);
        assert GroupOrderManager.getGroupOrder(arg2).getOrders().stream()
                .map(order -> ((SingleOrder) order).getUser().getUserId())
                .toList().contains(arg1);
    }

}
