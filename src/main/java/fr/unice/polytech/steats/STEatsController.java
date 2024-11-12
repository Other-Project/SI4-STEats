package fr.unice.polytech.steats;

import fr.unice.polytech.steats.users.UserManager;
import fr.unice.polytech.steats.utils.NotFoundException;

/**
 * @author Team C
 */

public class STEatsController {

    /**
     * Create the link between the user and the facade
     *
     * @param userId the username to log in
     * @return the facade associated with  the user
     */
    public STEats logging(String userId) throws NotFoundException {
        return new STEats(UserManager.getInstance().get(userId));
    }
}
