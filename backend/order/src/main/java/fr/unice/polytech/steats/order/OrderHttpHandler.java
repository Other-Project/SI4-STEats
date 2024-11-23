package fr.unice.polytech.steats.order;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.helpers.GroupOrderServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.IOrder;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Orders", path = "/api/orders")
public class OrderHttpHandler extends AbstractHandler {
    public OrderHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
    }

    @ApiRoute(path = "/", method = HttpUtils.GET, queryParams = {"restaurantId"})
    private void getAll(HttpExchange exchange, Map<String, String> params) throws IOException {
        String restaurantId = params.get("restaurantId");
        List<IOrder> orders = new ArrayList<>();

        if (restaurantId == null) {
            orders.addAll(GroupOrderServiceHelper.getAll());
            orders.addAll(SingleOrderServiceHelper.getAll());
        } else {
            orders.addAll(GroupOrderServiceHelper.getGroupOrdersByRestaurant(restaurantId));
            orders.addAll(SingleOrderServiceHelper.getSingleOrdersNotInGroupByRestaurant(restaurantId));
        }

        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, orders);
    }
}
