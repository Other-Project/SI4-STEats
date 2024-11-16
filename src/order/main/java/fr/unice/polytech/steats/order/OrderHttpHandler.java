package fr.unice.polytech.steats.order;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.helpers.GroupOrderServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.IOrder;
import fr.unice.polytech.steats.order.singles.SingleOrder;
import fr.unice.polytech.steats.order.singles.SingleOrderManager;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


// TODO : make a class that AbstractManager will inherit of

public class OrderHttpHandler extends AbstractManagerHandler<SingleOrderManager, SingleOrder> {
    public OrderHttpHandler(String subPath, Logger logger) {
        super(subPath, SingleOrder.class, logger);
    }

    @Override
    protected SingleOrderManager getManager() {
        return SingleOrderManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
    }

    private void getAll(HttpExchange exchange, Map<String, String> params) throws IOException {
        String restaurantId = params.get("restaurantId");

        if (restaurantId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }

        List<IOrder> orders = new ArrayList<>();
        orders.addAll(GroupOrderServiceHelper.getGroupOrdersByRestaurant(restaurantId));
        orders.addAll(SingleOrderServiceHelper.getSingleOrdersNotInGroupByRestaurant(restaurantId));

        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, orders);
    }
}
