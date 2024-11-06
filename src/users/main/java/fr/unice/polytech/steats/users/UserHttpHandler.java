package fr.unice.polytech.steats.users;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;

import java.util.logging.Logger;

public class UserHttpHandler extends AbstractManagerHandler<UserManager, User> {
    public UserHttpHandler(String subPath, Logger logger) {
        super(subPath, User.class, logger);
    }

    @Override
    protected UserManager getManager() {
        return UserManager.getInstance();
    }
}
