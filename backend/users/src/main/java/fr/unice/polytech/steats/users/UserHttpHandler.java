package fr.unice.polytech.steats.users;

import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpResponse;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.ApiBodyParam;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiPathParam;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Users", path = "/api/users")
public class UserHttpHandler extends AbstractHandler {
    public UserHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private UserManager getManager() {
        return UserManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "", description = "Get all users")
    public List<User> getAll() {
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", description = "Create a new user")
    public HttpResponse create(@ApiBodyParam User user) {
        getManager().add(user);
        return new HttpResponse(HttpUtils.CREATED_CODE);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", description = "Get a user by its id")
    public User get(@ApiPathParam(name = "id", description = "ID of the user") String id) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.DELETE, path = "/{id}", description = "Remove a user by its id")
    public void remove(@ApiPathParam(name = "id", description = "ID of the user to remove") String id) throws NotFoundException {
        getManager().remove(id);
    }
}
