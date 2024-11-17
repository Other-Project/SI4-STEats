package fr.unice.polytech.steats.helpers;


import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.models.SingleOrder;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;
import fr.unice.polytech.steats.utils.Status;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Helper class for calling the order service.
 *
 * @author Team C
 */
public class SingleOrderServiceHelper {
    public static final URI SINGLE_ORDER_SERVICE_URI = URI.create("http://localhost:5004/api/orders/singles/");

    private SingleOrderServiceHelper() {
    }

    /**
     * Get an order by its id.
     *
     * @param orderId The id of the order
     */
    public static SingleOrder getOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve(orderId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), SingleOrder.class);
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
        return JacksonUtils.listFromJson(response.body(), SingleOrder.class);
    }

    /**
     * Get all orders a specific group.
     *
     * @param groupCode The invitation code of the group
     */
    public static List<SingleOrder> getOrdersInGroup(String groupCode) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve("?groupCode=" + groupCode))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), SingleOrder.class);
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
        return JacksonUtils.listFromJson(response.body(), SingleOrder.class);
    }

    /**
     * Get all the single orders that are not in a group order of a restaurant.
     *
     * @param restaurantId The id of the restaurant
     */
    public static List<SingleOrder> getSingleOrdersNotInGroupByRestaurant(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve("?restaurantId=" + restaurantId + "&groupCode="))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), SingleOrder.class);
    }

    /**
     * Get all the single orders that are in a group order
     */
    public static List<SingleOrder> getAll() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve("?groupCode="))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), SingleOrder.class);
    }

    /**
     * Pay for an order.
     *
     * @param orderId The id of the order
     */
    public static Payment pay(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve("/pay"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("orderId", orderId))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Payment.class);
    }

    /**
     * Set status of an order.
     *
     * @param status The new status of the order
     */
    public static Status setStatus(String orderId, String status) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve(orderId + "/status"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("status", status))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Status.class);
    }

    /**
     * Set the delivery time of an order.
     *
     * @param orderId      The id of the order
     * @param deliveryTime The new delivery time of the order
     */
    public static LocalDateTime setDeliveryTime(String orderId, String deliveryTime) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SINGLE_ORDER_SERVICE_URI.resolve(orderId + "/deliveryTime"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("deliveryTime", deliveryTime))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), LocalDateTime.class);
    }
}
