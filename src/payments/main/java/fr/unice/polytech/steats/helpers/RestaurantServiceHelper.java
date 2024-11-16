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
    public static boolean canHandle(String restaurantId, LocalDateTime deliveryTime) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(RESTAURANT_SERVICE_URI.resolve(restaurantId + "/canHandle"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("deliveryTime", deliveryTime))))
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
}
