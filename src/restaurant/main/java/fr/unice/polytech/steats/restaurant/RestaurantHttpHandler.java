package fr.unice.polytech.steats.restaurant;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.logging.Logger;

public class RestaurantHttpHandler extends AbstractManagerHandler<RestaurantManager, Restaurant> {
    public RestaurantHttpHandler(String subPath, Logger logger) {
        super(subPath, Restaurant.class, logger);
    }

    @Override
    protected RestaurantManager getManager() {
        return RestaurantManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}/menu", (exchange, param) -> getMenu(exchange, param, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    private void getMenu(HttpExchange exchange, Map<String, String> param, Map<String, String> query) throws IOException {
        String restaurantId = param.get("id");
        String deliveryTimeString = query.get("deliveryTime");
        if (restaurantId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        Restaurant restaurant;
        try {
            restaurant = getManager().get(restaurantId);
        } catch (NotFoundException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
            return;
        }
        if (deliveryTimeString != null) {
            LocalDateTime deliveryTime;
            try {
                deliveryTime = LocalDateTime.parse(deliveryTimeString);
            } catch (DateTimeParseException e) {
                exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
                exchange.close();
                return;
            }
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.getAvailableMenu(deliveryTime));
        } else {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.getFullMenu());
        }
    }
}
