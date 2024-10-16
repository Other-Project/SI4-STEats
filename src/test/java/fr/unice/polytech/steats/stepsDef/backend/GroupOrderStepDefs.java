package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.STEats;
import fr.unice.polytech.steats.STEatsController;
import fr.unice.polytech.steats.order.*;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GroupOrderStepDefs {

    STEatsController stEatsController = new STEatsController();
    Map<String, STEats> steatsMap = new HashMap<>();

    @Before
    public void before() {
        GroupOrderManager.getInstance().clear();
        UserManager.getInstance().clear();
    }

    @Given("A group order with the group code {string} from the restaurant {string} and to deliver for {string} at {string}")
    public void a_group_order_is_created(String groupCode, String restaurant, String deliveryTime, String address) {
        GroupOrderManager.getInstance().add(groupCode, new GroupOrder(groupCode, LocalDateTime.parse(deliveryTime), new Address(address, "1", "1", "1"), new Restaurant(restaurant)));
    }

    @Given("The user named {string} with the id {string} is logged in")
    public void theUserWithTheIdIsLoggedIn(String name, String userId) throws NotFoundException {
        User user = new User(name, userId, Role.STUDENT);
        UserManager.getInstance().add(userId, user);
        steatsMap.put(userId, stEatsController.logging(userId));
    }

    @When("The user with the id {string} joins the group order with the group code {string}")
    public void the_user_joins_the_group_order(String userId, String groupCode) throws NotFoundException {
        steatsMap.get(userId).joinGroupOrder(groupCode);
    }

    @Then("The user with the id {string} is added to the group order with the group code {string}")
    public void the_user_is_added_to_the_group_order(String userId, String groupCode) throws NotFoundException {
        assertEquals(1, GroupOrderManager.getInstance().get(groupCode).getOrders().size());
        assertTrue(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .filter(order -> order instanceof SingleOrder)
                .map(order -> ((SingleOrder) order).getUserId())
                .toList()
                .contains(userId));
    }

    @When("The user with the id {string} adds the item named {string} with a price of {double} to the group order")
    public void theUserWithTheIdAddsTheItemNamedWithAPriceOfToTheGroupOrderWithTheGroupCode(String userId, String menuItem, double price) {
        steatsMap.get(userId).addMenuItem(new MenuItem(menuItem, price, Duration.ofMinutes(10)));
    }

    @Then("The item with named {string} with a price of {double} is added to the order of the user with the id {string} in the group order with the group code {string}")
    public void theItemWithNamedIsAddedToTheOrderOfTheUserWithTheIdInTheGroupOrderWithTheGroupCode(String menuItem, double price, String userId, String groupCode) throws NotFoundException {
        assertEquals(price, steatsMap.get(userId).getTotalPrice(), 0.1);
        assertTrue(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> order.getItems().size())
                .toList()
                .contains(1));
        assertTrue(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> order.getItems().getFirst().getName())
                .toList()
                .contains(menuItem));
        assertTrue(GroupOrderManager.getInstance().get(groupCode).getOrders().stream()
                .map(order -> ((SingleOrder) order).getUserId())
                .toList().contains(userId));
    }

    @When("The user with the id {string} pay")
    public void theUserWithTheIdPay(String userId) {
        steatsMap.get(userId).payOrder();
    }

    @Then("The order of the user with the id {string} is payed")
    public void theOrderOfTheUserWithTheIdIsPayed(String userId) {
        assertSame(Status.PAID, steatsMap.get(userId).getOrder().getStatus());
    }

    @And("The order is added to the history of the user with the id {string}")
    public void theOrderIsAddedToTheHistoryOfTheUserWithTheId(String userId) throws NotFoundException {
        assertEquals(1, UserManager.getInstance().get(userId).getOrders().size());
    }

    @And("The group order with the group code {string} is payed")
    public void theGroupOrderIsPayed(String groupCode) throws NotFoundException {
        assertSame(Status.PAID, GroupOrderManager.getInstance().get(groupCode).getStatus());
    }

    @And("The user with the id {string} can close the group order")
    public void heCanCloseTheGroupOrder(String userId) throws NotFoundException {
        assertTrue(steatsMap.get(userId).canCloseGroupOrder());
        assertDoesNotThrow(() -> steatsMap.get(userId).closeGroupOrder());
    }


    @And("The user with the id {string} can't close the group order")
    public void theUserWithTheIdCanTCloseTheGroupOrder(String userId) throws NotFoundException {
        assertFalse(steatsMap.get(userId).canCloseGroupOrder());
        assertThrows(IllegalStateException.class, () -> steatsMap.get(userId).closeGroupOrder());
    }

    @When("The user with the id {string} close the group order")
    public void theUserWithTheIdCloseTheGroupOrder(String userId) throws NotFoundException {
        steatsMap.get(userId).closeGroupOrder();
    }

    @Then("the group order with the id {string} is closed")
    public void theGroupOrderIsClosed(String groupeCode) throws NotFoundException {
        assertSame(Status.PAID, GroupOrderManager.getInstance().get(groupeCode).getStatus());
    }
}
