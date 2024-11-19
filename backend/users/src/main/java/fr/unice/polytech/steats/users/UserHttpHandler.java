package fr.unice.polytech.steats.users;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;

import java.util.logging.Logger;

@ApiMasterRoute(name = "Users", path = "/api/users")
public class UserHttpHandler extends AbstractManagerHandler<UserManager, User> {
    public UserHttpHandler(String subPath, Logger logger) {
        super(subPath, User.class, logger);
    }

    @Override
    protected UserManager getManager() {
        return UserManager.getInstance();
    }
}
