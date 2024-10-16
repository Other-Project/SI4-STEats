package fr.unice.polytech.steats;

import fr.unice.polytech.steats.user.NotFoundException;
import fr.unice.polytech.steats.user.User;
import fr.unice.polytech.steats.user.UserManager;

/**
 * @author Team C
 */

public class STEatsController {

    /**
     * Create the link between the user and the facade
     * @param userId the username to log in
     * @return the facade associated with  the user
     */
    public STEats logging(String userId) throws NotFoundException {
        try {
            User user = UserManager.getInstance().get(userId);
            return new STEats(user);
        } catch (NotFoundException e) {
            throw new NotFoundException("User " + userId + " not found");
        }
    }
}
