package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.helpers.GroupOrderServiceHelper;
import fr.unice.polytech.steats.helpers.SingleOrderServiceHelper;
import fr.unice.polytech.steats.models.IOrder;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiQueryParam;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ApiMasterRoute(name = "Orders", path = "/api/orders")
public class OrderHttpHandler extends AbstractHandler {
    public OrderHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    @ApiRoute(method = HttpUtils.GET, path = "", description = "Get all orders")
    public List<IOrder> getAll(
            @ApiQueryParam(name = "restaurantId", description = "The ID of the restaurant where the order was placed") String restaurantId
    ) throws IOException {
        if (restaurantId == null)
            return Stream.concat(GroupOrderServiceHelper.getAll().stream(), SingleOrderServiceHelper.getAll().stream()).map(o -> (IOrder) o).toList();
        else return Stream.concat(
                GroupOrderServiceHelper.getGroupOrdersByRestaurant(restaurantId).stream(),
                SingleOrderServiceHelper.getSingleOrdersNotInGroupByRestaurant(restaurantId).stream()
        ).map(o -> (IOrder) o).toList();
    }
}
