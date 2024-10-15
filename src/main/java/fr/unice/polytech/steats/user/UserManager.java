package fr.unice.polytech.steats.user;

/**
 * Emulates a database for users
 *
 * @author Team C
 */
public interface UserManager {

    /**
     * Add a user to the registry
     *
     * @param userName the name of the user
     * @param userId   the id of the user
     */
    void addUser(String userId, String userName, Role role);

    /**
     * Remove a user from the registry
     *
     * @param userId the id of the user
     */
    void removeUser(String userId);

    /**
     * Update a user from the registry
     *
     * @param userId   the new user id
     * @param user the new user
     */
    void updateUser(String userId, User user);
}
