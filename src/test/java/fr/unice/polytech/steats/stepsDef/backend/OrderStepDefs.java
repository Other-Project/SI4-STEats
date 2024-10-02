package fr.unice.polytech.steats.stepsDef.backend;

import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.user.Role;
import fr.unice.polytech.steats.user.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class OrderStepDefs {

    User user;
    SingleOrder order;

    public OrderStepDefs() {
    }

    @Given("an user of name {string} and with userId {string}")
    public void givenAnUser(String userName, String userId) {
        user = new User(userName, userId, Role.REGISTERED_USER);
    }

    @When("{string} creates an order and specifies a date")
    public void whenCreatesOrder(String userName) {

    }

    @Then("A new order is created")
    public void thenOrderIsCreated() {

    }

    @And("{string} sees only available menuItems")
    public void seesOnlyAvailableMenuItems(String userName) {
        user = new User("140403", userName, Role.REGISTERED_USER);
    }
}
