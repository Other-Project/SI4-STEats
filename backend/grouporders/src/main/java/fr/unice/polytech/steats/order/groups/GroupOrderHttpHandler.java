package fr.unice.polytech.steats.order.groups;

import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpResponse;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Group Orders", path = "/api/orders/groups")
public class GroupOrderHttpHandler extends AbstractHandler {
    public GroupOrderHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private GroupOrderManager getManager() {
        return GroupOrderManager.getInstance();
    }

    @ApiRoute(path = "", method = HttpUtils.GET, summary = "Get all group orders")
    public List<GroupOrder> getAll(
            @ApiQueryParam(name = "restaurantId") String restaurantId
    ) {
        if (restaurantId != null) return getManager().getOrdersByRestaurant(restaurantId);
        return getManager().getAll();
    }

    @ApiRoute(path = "", method = HttpUtils.PUT, summary = "Add a new group order")
    public HttpResponse add(
            @ApiBodyParam GroupOrder groupOrder
    ) {
        getManager().add(groupOrder);
        return new HttpResponse(HttpUtils.CREATED_CODE);
    }

    @ApiRoute(path = "/{id}", method = HttpUtils.GET, summary = "Get a group order by its ID")
    public GroupOrder get(
            @ApiPathParam(name = "id", description = "ID of the group order to get") String id
    ) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(path = "/{id}/close", method = HttpUtils.POST, summary = "Closes a group order")
    public HttpResponse close(
            @ApiPathParam(name = "id", description = "ID of the group order to close") String id
    ) throws IOException, NotFoundException {
        GroupOrderManager.getInstance().get(id).closeOrder();
        return new HttpResponse(HttpUtils.NO_CONTENT_CODE);
    }

    @ApiRoute(path = "/{id}/users", method = HttpUtils.GET, summary = "Get all users' id of a group order")
    public List<String> getUsers(
            @ApiPathParam(name = "id", description = "ID of the group order") String id
    ) throws IOException {
        return getManager().getUsers(id);
    }
}
