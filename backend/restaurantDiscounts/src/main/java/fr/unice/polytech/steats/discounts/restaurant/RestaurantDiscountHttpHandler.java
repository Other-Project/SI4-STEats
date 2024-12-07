package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.helpers.UserServiceHelper;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.models.Status;
import fr.unice.polytech.steats.models.User;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiPathParam;
import fr.unice.polytech.steats.utils.openapi.ApiQueryParam;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Restaurant discounts", path = "/api/discounts/restaurant")
public class RestaurantDiscountHttpHandler extends AbstractHandler {
    public RestaurantDiscountHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private RestaurantDiscountManager getManager() {
        return RestaurantDiscountManager.getInstance();
    }

    @ApiRoute(path = "", method = HttpUtils.GET, summary = "Get all discounts")
    public List<Discount> getAll(
            @ApiQueryParam(name = "orderId", description = "Only discounts applicable to the order of the given ID") String orderId,
            @ApiQueryParam(name = "restaurantId", description = "Only discounts from the restaurant of the given ID") String restaurantId
    ) throws IOException {
        if (orderId != null)
            return applicableDiscounts(orderId);
        else if (restaurantId != null)
            return getManager().getDiscountsOfRestaurant(restaurantId);
        return getManager().getAll();
    }

    @ApiRoute(path = "/{id}", method = HttpUtils.GET, summary = "Get a discount by ID")
    public Discount get(
            @ApiPathParam(name = "id", description = "ID of the discount") String id
    ) throws NotFoundException {
        return getManager().get(id);
    }

    private List<Discount> applicableDiscounts(String orderId) throws IOException {
        SingleOrder order = SingleOrderServiceHelper.getOrder(orderId);
        User user = UserServiceHelper.getUser(order.userId());
        List<SingleOrder> orders = SingleOrderServiceHelper.getOrdersByUserInRestaurantPastStatus(order.userId(), order.restaurantId(), Status.PAID);
        return getManager().getApplicableDiscounts(order, user, orders);
    }
}
