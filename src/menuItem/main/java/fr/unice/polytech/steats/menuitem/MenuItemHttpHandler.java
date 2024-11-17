package fr.unice.polytech.steats.menuitem;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class MenuItemHttpHandler extends AbstractManagerHandler<MenuItemManager, MenuItem> {

    protected MenuItemHttpHandler(String subPath, Logger logger) {
        super(subPath, MenuItem.class, logger);
    }

    @Override
    protected MenuItemManager getManager() {
        return MenuItemManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), ((exchange, params) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery()))));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    private void getAll(HttpExchange exchange, Map<String, String> query) throws IOException {
        if (query.containsKey("restaurantId")) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getByRestaurant(query.get("restaurantId")));
        } else {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
        }
    }

}
