package fr.unice.polytech.steats.helpers;

//import fr.unice.polytech.steats.discount.Discount;

import fr.unice.polytech.steats.models.RestaurantDiscount;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper class for calling the Discount service.
 *
 * @author Team C
 */
public class DiscountServiceHelper {

    public static final URI DISCOUNT_SERVICE_URI = URI.create("http://localhost:5009/api/discounts/restaurant/");

    private DiscountServiceHelper() {
    }


    /**
     * Get a discount by its id.
     *
     * @param discountId The id of the discount
     */
    public static RestaurantDiscount getDiscount(String discountId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(DISCOUNT_SERVICE_URI.resolve(discountId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), RestaurantDiscount.class);

    }

    /**
     * Get the discount to apply next.
     *
     * @param userId       The id of the user
     * @param restaurantId The id of the restaurant
     */
    /*public static List<Discount> getDiscountToApplyNext(String userId, String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(DISCOUNT_SERVICE_URI.resolve("?userId=" + userId + "&restaurantId=" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Discount.class);
    }*/
}
