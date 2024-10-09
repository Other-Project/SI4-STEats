package fr.unice.polytech.steats.user;

import java.util.List;
import java.util.Optional;

/**
 * @param <T>
 * @author team-C
 */
public interface UserFinder<T extends UserInterface> {

    /**
     * Find all users
     *
     * @return a list of all Users
     */
    List<T> findAll();

    /**
     * Find a user by its name
     *
     * @param name the name to search
     * @return an optional with the user
     */
    Optional<T> findByName(String name);

    /**
     * Find a user by its id
     *
     * @param userId the id to search
     * @return an optional with the user
     */
    Optional<T> findById(String userId);
}
