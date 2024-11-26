package fr.unice.polytech.steats.discounts.restaurant;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.helpers.UserServiceHelper;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.models.Status;
import fr.unice.polytech.steats.models.User;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Restaurant discounts", path = "/api/discounts/restaurant")
public class RestaurantDiscountHttpHandler extends AbstractManagerHandler<RestaurantDiscountManager, Discount> {
    public RestaurantDiscountHttpHandler(String subPath, Logger logger) {
        super(subPath, Discount.class, logger);
    }

    @Override
    protected RestaurantDiscountManager getManager() {
        return RestaurantDiscountManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, params) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/api/discounts/restaurant", summary = "Get all discounts")
    private void getAll(HttpExchange exchange, Map<String, String> query) throws IOException {
        String orderId = query.get("orderId");
        String restaurantId = query.get("restaurantId");
        if (orderId != null)
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, applicableDiscounts(orderId));
        else if (restaurantId != null)
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getDiscountsOfRestaurant(restaurantId));
        else HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
    }

    private List<Discount> applicableDiscounts(String orderId) throws IOException {
        SingleOrder order = SingleOrderServiceHelper.getOrder(orderId);
        User user = UserServiceHelper.getUser(order.userId());
        List<SingleOrder> orders = SingleOrderServiceHelper.getOrdersByUserInRestaurantPastStatus(order.userId(), order.restaurantId(), Status.PAID);
        return getManager().getApplicableDiscounts(order, user, orders);
    }
}
