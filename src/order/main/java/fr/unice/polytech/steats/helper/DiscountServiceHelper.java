package fr.unice.polytech.steats.helper;

import fr.unice.polytech.steats.discounts.Discount;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Helper class for calling the MenuItem service.
 *
 * @author Team C
 */
public class DiscountServiceHelper {

    public static final URI ORDER_SERVICE_URI = URI.create("http://localhost:5006/api/orders");

    private DiscountServiceHelper() {
    }

    /**
     * Get a menu item by its id.
     *
     * @param menuItemId The id of the menu item
     */
    public static Discount getDiscount(String menuItemId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve(menuItemId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Discount.class);

    }

    /**
     * Get the discount to apply next.
     *
     * @param userId       The id of the user
     * @param restaurantId The id of the restaurant
     */
    public static List<Discount> getDiscountToApplyNext(String userId, String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve("?userId=" + userId + "&restaurantId=" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body());
    }
}
