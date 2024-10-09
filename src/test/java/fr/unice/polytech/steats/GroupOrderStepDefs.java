package fr.unice.polytech.steats;

import fr.unice.polytech.steats.order.Address;
import fr.unice.polytech.steats.order.GroupOrderManager;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;

public class GroupOrderStepDefs {

    @Given("A group order is created with the group code {string} and the delivery time {string} and the address {string} and the restaurant {string}")
    public void a_group_order_is_created(String groupCode, String deliveryTime, String address, String restaurant) {
        STEats steats = new STEats(new User("Alban", "12345", Role.EXTERNAL));
        steats.createGroupOrder(groupCode, LocalDateTime.parse(deliveryTime), new Address(address, "Antibes", "06600", "villa_116"), new Restaurant(restaurant));
    }

    @When("The user with the id {string} joins the group order with the group code {string}")
    public void the_user_joins_the_group_order(String userId, String groupCode) {
        User user = new User("Alex", userId, Role.STUDENT);
        STEats steats = new STEats(user);
        steats.joinGroupOrder(groupCode);
    }

    @Then("The user with the id {string} is added to the group order with the group code {string}")
    public void the_user_is_added_to_the_group_order(String userId, String groupCode) {
        assert GroupOrderManager.getGroupOrder(groupCode).getOrders().size() == 2;
        assert GroupOrderManager.getGroupOrder(groupCode).getOrders().stream()
                .filter(order -> order instanceof SingleOrder)
                .map(order -> ((SingleOrder) order).getUserId())
                .toList()
                .contains(userId);
    }
}
