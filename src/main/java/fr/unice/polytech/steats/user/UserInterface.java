package fr.unice.polytech.steats.user;

/**
 * User Interface
 *
 * @author Team C
 */
public interface UserInterface {
    /**
     * Get the user's name
     */
    String getName();

    /**
     * Get the user's id
     */
    String getUserId();

    /**
     * Set the name of user
     *
     * @param name the name which replace the former user's name
     */
    void setName(String name);

    /**
     * Set the user id
     *
     * @param userId the user id which replace the former user's id
     */
    void setUserId(String userId);

    /**
     * Get the user's role
     *
     * @return the user's role
     */
    Role getRole();
}
