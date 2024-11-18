package fr.unice.polytech.steats.order.singles;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.*;

import java.io.IOException;
import java.time.LocalDateTime;
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
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/status", this::setStatus);
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/deliveryTime", this::setDeliveryTime);
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
        String orderId = params.get("id");
        Payment payment = null;
        try {
            payment = SingleOrderManager.getInstance().get(orderId).pay();
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
            exchange.close();
        }
        if (payment == null) {
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, -1);
            exchange.close();
        } else HttpUtils.sendJsonResponse(exchange, HttpUtils.CREATED_CODE, payment);
    }

    private void create(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
            SingleOrder singleOrder = JacksonUtils.fromJson(exchange.getRequestBody(), SingleOrder.class);
            if (!singleOrder.checkGroupOrder()) {
                exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
                exchange.close();
                return;
            }
            getManager().add(singleOrder);
            exchange.sendResponseHeaders(HttpUtils.CREATED_CODE, -1);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
        }
        exchange.getResponseBody().close();
    }

    private void setStatus(HttpExchange exchange, Map<String, String> params) throws IOException {
        String orderId = params.get("id");
        Map<String, Object> body = JacksonUtils.mapFromJson(exchange.getRequestBody());
        String status = body == null ? null : body.get("status").toString();

        if (status == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.getResponseBody().close();
            return;
        }

        try {
            SingleOrderManager.getInstance().get(orderId).setStatus(Status.valueOf(status));
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
        }
        exchange.sendResponseHeaders(HttpUtils.OK_CODE, -1);
        exchange.getResponseBody().close();
    }

    private void setDeliveryTime(HttpExchange exchange, Map<String, String> params) throws IOException {
        String orderId = params.get("id");
        Map<String, Object> body = JacksonUtils.mapFromJson(exchange.getRequestBody());
        String deliveryTime = body == null ? null : body.get("deliveryTime").toString();

        if (deliveryTime == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.getResponseBody().close();
            return;
        }

        try {
            SingleOrderManager.getInstance().get(orderId).setDeliveryTime(LocalDateTime.parse(deliveryTime));
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
        }
        exchange.sendResponseHeaders(HttpUtils.OK_CODE, -1);
        exchange.getResponseBody().close();
    }
}