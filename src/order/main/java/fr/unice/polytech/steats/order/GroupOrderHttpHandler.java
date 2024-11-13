package fr.unice.polytech.steats.order;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class GroupOrderHttpHandler extends AbstractManagerHandler<GroupOrderManager, GroupOrder> {
    public GroupOrderHttpHandler(String subPath, Logger logger) {
        super(subPath, GroupOrder.class, logger);
    }

    @Override
    protected GroupOrderManager getManager() {
        return GroupOrderManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/{id}/close", this::close);
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    private void getAll(HttpExchange exchange, Map<String, String> params) throws IOException {
        String restaurantId = params.get("restaurantId");

        if (restaurantId != null) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getOrdersByRestaurant(restaurantId));
        } else {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
        }
    }

    private void close(HttpExchange exchange, Map<String, String> params) throws IOException {
        String groupOrderId = params.get("groupOrderId");

        if (groupOrderId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, 0);
            exchange.close();
            return;
        }
        try {
            GroupOrderManager.getInstance().get(groupOrderId).closeOrder();
        } catch (IllegalStateException e) {
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, 0);
            exchange.close();
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
            exchange.close();
        }
    }
}
