package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.GroupOrder;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Helper class for calling the group order service.
 *
 * @author Team C
 */
public class GroupOrderServiceHelper {
    public static final URI GROUP_ORDER_SERVICE_URI = URI.create("http://localhost:5005/api/orders/groups/");

    private GroupOrderServiceHelper() {
    }

    /**
     * Get all user ids in a group order.
     *
     * @param groupCode The invitation code of the group
     */
    public static List<String> getUsersInGroup(String groupCode) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(GROUP_ORDER_SERVICE_URI.resolve(groupCode + "/users"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), String.class);
    }

    /**
     * Get a group order by its id.
     *
     * @param groupCode The id of the group order
     */
    public static GroupOrder getGroupOrder(String groupCode) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(GROUP_ORDER_SERVICE_URI.resolve(groupCode))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), GroupOrder.class);
    }

    /**
     * Get all group orders
     */
    public static List<GroupOrder> getAll() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(GROUP_ORDER_SERVICE_URI)
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), GroupOrder.class);
    }

    /**
     * Get all group orders in a specific restaurant.
     *
     * @param restaurantId The id of the restaurant
     */
    public static List<GroupOrder> getGroupOrdersByRestaurant(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(GROUP_ORDER_SERVICE_URI.resolve("?restaurantId=" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), GroupOrder.class);
    }
}
