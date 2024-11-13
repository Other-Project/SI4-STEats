package fr.unice.polytech.steats.order;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.*;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class SingleOrderHttpHandler extends AbstractManagerHandler<SingleOrderManager, SingleOrder> {
    public SingleOrderHttpHandler(String subPath, Logger logger) {
        super(subPath, SingleOrder.class, logger);
    }

    @Override
    protected SingleOrderManager getManager() {
        return SingleOrderManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> create(exchange));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/pay", this::pay);
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    private void getAll(HttpExchange exchange, Map<String, String> params) throws IOException {
        String userId = params.get("userId");
        String restaurantId = params.get("restaurantId");
        String groupCode = params.get("groupCode");

        if (userId != null && restaurantId != null && groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByUserInRestaurant(userId, restaurantId, groupCode));
        } else if (userId != null && groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByUser(userId, groupCode));
        } else if (restaurantId != null && groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByRestaurant(restaurantId, groupCode));
        } else if (userId != null && restaurantId != null) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByUserInRestaurant(userId, restaurantId));
        } else if (userId != null) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByUser(userId));
        } else if (restaurantId != null) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByRestaurant(restaurantId));
        } else if (groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByGroup(groupCode));
        } else {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
        }
    }

    private void pay(HttpExchange exchange, Map<String, String> params) throws IOException {
        String orderId = params.get("orderId");

        if (orderId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, 0);
            exchange.close();
            return;
        }
        Payment payment = null;
        try {
            payment = SingleOrderManager.getInstance().get(orderId).pay();
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
            exchange.close();
        }
        if (payment == null) {
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, 0);
            exchange.close();
        } else HttpUtils.sendJsonResponse(exchange, HttpUtils.CREATED_CODE, payment);
    }

    private void create(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
            SingleOrder singleOrder = JacksonUtils.fromJson(exchange.getRequestBody(), SingleOrder.class);
            singleOrder.checkGroupOrder();
            getManager().add(singleOrder);
            exchange.sendResponseHeaders(HttpUtils.CREATED_CODE, -1);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
        }
        exchange.getResponseBody().close();
    }
}
