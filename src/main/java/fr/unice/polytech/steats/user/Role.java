package fr.unice.polytech.steats.user;


/**
 * Represents the role of the user.
 *
 * @author Team C
 */
public enum Role {
    /**
     * A student at the campus
     */
    STUDENT,
    /**
     * Faculty members
     */
    FACULTY,
    /**
     * Restaurant staff that manages the orders
     */
    RESTAURANT_STAFF,
    /**
     * Restaurant managers that can update opening hours and menu offerings
     */
    RESTAURANT_MANAGER,
    /**
     * Users that are not faculty members. Ex : High-school student during open house day.
     */
    EXTERNAL,
}
