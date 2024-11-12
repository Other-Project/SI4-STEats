package fr.unice.polytech.steats.helper;

import fr.unice.polytech.steats.order.Order;
import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.restaurant.Restaurant;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Helper class for calling the MenuItem service.
 *
 * @author Team C
 */
public class RestaurantServiceHelper {

    public static final URI ORDER_SERVICE_URI = URI.create("http://localhost:5007/api/orders");

    private RestaurantServiceHelper() {
    }

    /**
     * Get a restaurant by its id.
     *
     * @param menuItemId The id of the menu item
     */
    public static Restaurant getRestaurant(String menuItemId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve(menuItemId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Restaurant.class);

    }

    /**
     * Check if the restaurant can handle the order.
     *
     * @param id           The id of the restaurant
     * @param deliveryTime The time the client wants the order to be delivered
     */
    public static boolean canHandle(String id, LocalDateTime deliveryTime) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve("?id=" + id + "&deliveryTime=" + deliveryTime))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Boolean.class);
    }

    /**
     * Add an order.
     *
     * @param id The id of the order
     */
    public static Order addOrder(String id) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve("/addOrder"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("orderId", id))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Order.class);
    }

    /**
     * Get the full menu of a restaurant.
     *
     * @param restaurantId The id of the restaurant
     */
    public static List<MenuItem> getFullMenu(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve("/menu/" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body());
    }
}
