package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Order;
import fr.unice.polytech.steats.order.SingleOrder;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Helper class for calling the order service.
 *
 * @author Team C
 */
public class SingleOrderServiceHelper {
    public static final URI SINGLE_ORDER_SERVICE_URI = URI.create("http://localhost:5004/api/orders/singles");

    private SingleOrderServiceHelper() {
    }

    /**
     * Get an order by its id.
     *
     * @param orderId The id of the order
     */
    public static Order getOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve(orderId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Order.class);
    }

    /**
     * Get all orders of a user in a specific restaurant.
     *
     * @param userId       The id of the user
     * @param restaurantId The id of the restaurant
     */
    public static List<SingleOrder> getOrdersByUserInRestaurant(String userId, String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve("?userId=" + userId + "&restaurantId=" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body());
    }

    /**
     * Get all orders of a user.
     *
     * @param userId The id of the user
     */
    public static List<SingleOrder> getOrdersByUser(String userId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve("?userId=" + userId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body());
    }
}
