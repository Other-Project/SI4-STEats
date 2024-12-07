package fr.unice.polytech.steats.menuitem;

import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpResponse;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Menu items", path = "/api/menu-items")
public class MenuItemHttpHandler extends AbstractHandler {

    protected MenuItemHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private MenuItemManager getManager() {
        return MenuItemManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "", summary = "Get all menu items")
    public List<MenuItem> getAll(
            @ApiQueryParam(name = "restaurantId", description = "ID of the restaurant where the menu items are served") String restaurantId
    ) {
        if (restaurantId != null) return getManager().getByRestaurant(restaurantId);
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", summary = "Create a new menu item", successStatus = HttpUtils.CREATED_CODE)
    public HttpResponse create(@ApiBodyParam MenuItem menuItem) {
        getManager().add(menuItem);
        return new HttpResponse(HttpUtils.CREATED_CODE);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", summary = "Get a menu item by its id")
    public MenuItem get(@ApiPathParam(name = "id", description = "ID of the menu item") String id) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.DELETE, path = "/{id}", summary = "Remove a menu item by its id")
    public void remove(@ApiPathParam(name = "id", description = "ID of the menu item to remove") String id) throws NotFoundException {
        getManager().remove(id);
    }
}
