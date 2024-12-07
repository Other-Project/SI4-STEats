package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.models.Restaurant;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Helper class for calling the Restaurant service.
 *
 * @author Team C
 */
public class RestaurantServiceHelper {

    public static final URI RESTAURANT_SERVICE_URI = URI.create("http://localhost:5006/api/restaurants/");

    private RestaurantServiceHelper() {
    }

    /**
     * Get a restaurant by its id.
     *
     * @param restaurantId The id of the restaurant
     */
    public static Restaurant getRestaurant(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(RESTAURANT_SERVICE_URI.resolve(restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Restaurant.class);

    }

    /**
     * Check if the restaurant can handle the order.
     *
     * @param restaurantId The id of the restaurant
     * @param deliveryTime The time the client wants the order to be delivered
     */
    public static boolean canHandle(String restaurantId, Duration preparationTime, LocalDateTime deliveryTime) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(RESTAURANT_SERVICE_URI.resolve(restaurantId + "/canHandle"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("deliveryTime", deliveryTime, "preparationTime", preparationTime))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Boolean.class);
    }

    /**
     * Get the full menu of a restaurant.
     *
     * @param restaurantId The id of the restaurant
     */
    public static List<MenuItem> getFullMenu(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(RESTAURANT_SERVICE_URI.resolve(restaurantId + "/menu"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), MenuItem.class);
    }

    /**
     * Check if the restaurant can add an order.
     *
     * @param restaurantId The id of the restaurant
     * @param deliveryTime The delivery time of the order
     */
    public static boolean canAddOrder(String restaurantId, LocalDateTime deliveryTime) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(RESTAURANT_SERVICE_URI.resolve(restaurantId + "/canAddOrder"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("deliveryTime", deliveryTime))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Boolean.class);
    }

    /**
     * Check if the restaurant can handle a quantity of menuItems represented by their total preparation time at a given time
     *
     * @param restaurantId    The id of the restaurant
     * @param preparationTime The time it takes to prepare the additional menuItems
     * @param deliveryTime    The time of delivery
     */
    public static boolean canHandlePreparationTime(String restaurantId, Duration preparationTime, LocalDateTime deliveryTime) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(RESTAURANT_SERVICE_URI.resolve(restaurantId + "/canHandlePreparationTime"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("deliveryTime", deliveryTime, "preparationTime", preparationTime))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Boolean.class);
    }
}
