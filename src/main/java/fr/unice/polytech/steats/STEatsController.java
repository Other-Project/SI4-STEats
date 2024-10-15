package fr.unice.polytech.steats;

import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserNotFoundException;
import fr.unice.polytech.steats.user.UserRegistry;

/**
 * @author Team C
 */

public class STEatsController {
    private final UserRegistry userRegistry;

    public STEatsController(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    /**
     * Create the link between the user and the facade
     * @param user the user to log in
     * @return the facade associated with  the user
     */
    public STEats logging(User user) throws UserNotFoundException {
        if (userRegistry.findByName(user.getName()).isPresent()) {
            return new STEats(user);
        } else throw new UserNotFoundException("User " + user.getName() + " not found");
    }
}
