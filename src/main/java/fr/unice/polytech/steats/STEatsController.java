package fr.unice.polytech.steats;

import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserRegistry;

import java.util.Optional;

/**
 * @author Team C
 */

public class STEatsController {
    public static final UserRegistry USER_REGISTRY = new UserRegistry();

    /**
     * Create the link between the user and the facade
     * @param userName the username to log in
     * @return the facade associated with  the user
     */
    public STEats logging(String userName) throws NotFoundException {
        Optional<User> user = USER_REGISTRY.findByName(userName);
        if (user.isPresent()) {
            return new STEats(user.get());
        } else throw new NotFoundException("User " + userName + " not found");
    }
}
