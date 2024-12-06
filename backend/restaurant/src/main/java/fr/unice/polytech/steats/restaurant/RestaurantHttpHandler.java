package fr.unice.polytech.steats.restaurant;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.*;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Restaurants", path = "/api/restaurants")
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
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}/opening-times/{dayOfWeek}", this::getOpeningTimes);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/canHandle", this::canHandle);
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/canAddOrder", this::canAddOrder);
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/canHandlePreparationTime", this::canHandlePreparationTime);
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    @ApiRoute(path = "/{id}/canHandlePreparationTime", method = HttpUtils.POST, body = {"preparationTime", "deliveryTime"})
    private void canHandlePreparationTime(HttpExchange exchange, Map<String, String> param) throws IOException {
        String restaurantId = param.get("id");
        Map<String, Object> body = JacksonUtils.mapFromJson(exchange.getRequestBody());
        Restaurant restaurant;
        try {
            restaurant = getManager().get(restaurantId);
        } catch (NotFoundException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
            return;
        }
        Duration preparationTime;
        LocalDateTime deliveryTime;
        try {
            String preparationTimeString = body.get("preparationTime").toString();
            String deliveryTimeString = body.get("deliveryTime").toString();
            preparationTime = Duration.parse(preparationTimeString);
            deliveryTime = LocalDateTime.parse(deliveryTimeString);
        } catch (NullPointerException | DateTimeParseException e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.canHandlePreparationTime(preparationTime, deliveryTime));
    }

    @ApiRoute(path = "/{id}/canAddOrder", method = HttpUtils.POST, body = {"deliveryTime"})
    private void canAddOrder(HttpExchange exchange, Map<String, String> param) throws IOException {
        String restaurantId = param.get("id");
        Map<String, Object> body = JacksonUtils.mapFromJson(exchange.getRequestBody());
        Restaurant restaurant;
        try {
            restaurant = getManager().get(restaurantId);
        } catch (NotFoundException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
            return;
        }
        LocalDateTime deliveryTime;
        try {
            String deliveryTimeString = body.get("deliveryTime").toString();
            deliveryTime = LocalDateTime.parse(deliveryTimeString);
        } catch (NullPointerException | DateTimeParseException e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.canAddOrder(deliveryTime));
    }

    @ApiRoute(path = "/{id}/canHandle", method = HttpUtils.POST, body = {"preparationTime", "deliveryTime"})
    private void canHandle(HttpExchange exchange, Map<String, String> param) throws IOException {
        String restaurantId = param.get("id");
        Map<String, Object> body = JacksonUtils.mapFromJson(exchange.getRequestBody());
        Restaurant restaurant;
        try {
            restaurant = getManager().get(restaurantId);
        } catch (NotFoundException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
            return;
        }
        Duration preparationTime;
        LocalDateTime deliveryTime;
        try {
            String preparationTimeString = body.get("preparationTime").toString();
            String deliveryTimeString = body.get("deliveryTime").toString();
            preparationTime = Duration.parse(preparationTimeString);
            deliveryTime = LocalDateTime.parse(deliveryTimeString);
        } catch (NullPointerException | DateTimeParseException e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.canHandle(preparationTime, deliveryTime));
    }

    @ApiRoute(path = "/{id}/opening-times/{dayOfWeek}", method = HttpUtils.GET)
    private void getOpeningTimes(HttpExchange exchange, Map<String, String> param) throws IOException {
        String restaurantId = param.get("id");
        String dayOfWeek = param.get("dayOfWeek");
        DayOfWeek day;
        try {
            day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        } catch (IllegalArgumentException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, "Invalid day of week");
            return;
        }
        try {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().get(restaurantId).getOpeningTimes(day));
        } catch (NotFoundException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
        }
    }

    @ApiRoute(path = "/{id}/menu", method = HttpUtils.GET, queryParams = {"deliveryTime", "orderPreparationTime"})
    private void getMenu(HttpExchange exchange, Map<String, String> param, Map<String, String> query) throws IOException {
        String restaurantId = param.get("id");
        String deliveryTimeString = query.get("deliveryTime");
        String orderPreparationTimeString = query.get("orderPreparationTime");
        Restaurant restaurant;
        try {
            restaurant = getManager().get(restaurantId);
        } catch (NotFoundException e) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
            return;
        }
        if (deliveryTimeString != null) {
            LocalDateTime deliveryTime;
            Duration orderPreparationTime;
            try {
                deliveryTime = LocalDateTime.parse(deliveryTimeString);
                orderPreparationTime = Duration.parse(orderPreparationTimeString);
            } catch (DateTimeParseException e) {
                exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
                exchange.close();
                return;
            }
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.getAvailableMenu(deliveryTime, orderPreparationTime));
        } else {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, restaurant.getFullMenu());
        }
    }
}
