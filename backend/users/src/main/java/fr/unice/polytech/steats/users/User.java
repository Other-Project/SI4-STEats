package fr.unice.polytech.steats.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.unice.polytech.steats.models.Role;

/**
 * The person that is currently using the system
 *
 * @author Team C
 */
public class User {
    private String name;
    private final String userId;
    private final Role role;

    /**
     * @param name   The name of the user
     * @param userId The id of the user
     * @param role   The role of the user
     */
    public User(@JsonProperty("name") String name, @JsonProperty("id") String userId, @JsonProperty("role") Role role) {
        this.name = name;
        this.userId = userId;
        this.role = role;
    }

    /**
     * Get username
     */
    public String getName() {
        return name;
    }

    /**
     * Get user id
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Update username
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the user's role
     */
    public Role getRole() {
        return role;
    }

// TODO: Migrate this

//    /**
//     * Get the discounts to apply to the next order
//     *
//     * @param restaurantId The id of the restaurant where the user wants to order
//     */
//    public List<Discount> getDiscountsToApplyNext(String restaurantId) {
//        List<SingleOrder> orders = getOrders(restaurantId);
//        return orders.isEmpty() ? Collections.emptyList() : orders.getLast().getDiscountsToApplyNext();
//    }
}
