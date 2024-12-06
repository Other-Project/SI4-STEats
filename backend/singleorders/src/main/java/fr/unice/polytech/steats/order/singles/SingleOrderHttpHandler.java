package fr.unice.polytech.steats.order.singles;

import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.models.Status;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JsonResponse;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Single Orders", path = "/api/orders/singles")
public class SingleOrderHttpHandler extends AbstractHandler {

    public SingleOrderHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private SingleOrderManager getManager() {
        return SingleOrderManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "", summary = "Get all single orders")
    public List<SingleOrder> getAll(
            @ApiQueryParam(name = "userId") String userId,
            @ApiQueryParam(name = "restaurantId") String restaurantId,
            @ApiQueryParam(name = "groupCode") String groupCode
    ) {
        if (userId != null && restaurantId != null && groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            return getManager().getOrdersByUserInRestaurant(userId, restaurantId, groupCode);
        } else if (userId != null && groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            return getManager().getOrdersByUser(userId, groupCode);
        } else if (restaurantId != null && groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            return getManager().getOrdersByRestaurant(restaurantId, groupCode);
        }
        if (userId != null && restaurantId != null)
            return getManager().getOrdersByUserInRestaurant(userId, restaurantId);
        if (userId != null)
            return getManager().getOrdersByUser(userId);
        if (restaurantId != null)
            return getManager().getOrdersByRestaurant(restaurantId);
        if (groupCode != null) {
            if (groupCode.isEmpty()) groupCode = null;
            return getManager().getOrdersByGroup(groupCode);
        }
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.POST, path = "", summary = "Join a group order")
    public JsonResponse<SingleOrder> create(
            @ApiBodyParam(name = "userId") String userId,
            @ApiBodyParam(name = "groupCode") String groupCode
    ) throws IOException {
        return create(new SingleOrder(userId, groupCode));
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", summary = "Create a single order")
    public JsonResponse<SingleOrder> create(@ApiBodyParam SingleOrder singleOrder) throws IOException {
        if (!singleOrder.checkGroupOrder())
            throw new IllegalArgumentException("The delivery time, the address or the restaurant differs from what's defined in the group order");
        getManager().add(singleOrder);
        return new JsonResponse<>(HttpUtils.CREATED_CODE, singleOrder);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", summary = "Get a single order by its ID")
    public SingleOrder get(
            @ApiPathParam(name = "id", description = "ID of the single order") String id
    ) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/pay", summary = "Pay a single order")
    public Payment pay(
            @ApiPathParam(name = "id", description = "ID of the single order") String id
    ) throws IOException, NotFoundException {
        Payment payment = SingleOrderManager.getInstance().get(id).pay();
        if (payment == null) throw new IOException("Payment failed");
        return payment;
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/status", summary = "Set the status of a single order")
    public void setStatus(
            @ApiPathParam(name = "id", description = "ID of the single order") String id,
            @ApiBodyParam(name = "status") Status status
    ) throws NotFoundException {
        SingleOrderManager.getInstance().get(id).setStatus(status);
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/deliveryTime", summary = "Set the delivery time of a single order")
    public void setDeliveryTime(
            @ApiPathParam(name = "id", description = "ID of the single order") String id,
            @ApiBodyParam(name = "deliveryTime") LocalDateTime deliveryTime
    ) throws NotFoundException {
        SingleOrderManager.getInstance().get(id).setDeliveryTime(deliveryTime);
    }

    @ApiRoute(method = HttpUtils.POST, path = "/{id}/modifyCartItem", summary = "Add/Modify/Remove a menu item in cart")
    public SingleOrder modifyCartItem(
            @ApiPathParam(name = "id", description = "ID of the single order") String id,
            @ApiBodyParam(name = "menuItem", description = "The menu item to add/modify/remove form the cart") String menuItem,
            @ApiBodyParam(name = "quantity", description = "Quantity to set (0 to remove)") int quantity
    ) throws NotFoundException, IOException {
        SingleOrder order = SingleOrderManager.getInstance().get(id);
        order.modifyMenuItem(menuItem, quantity);
        return order;
    }
}
