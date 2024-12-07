package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.RestaurantDiscount;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

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
     * Get discounts for an order.
     *
     * @param orderId The id of the order
     */
    public static List<RestaurantDiscount> getDiscountsForOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(DISCOUNT_SERVICE_URI.resolve("?orderId=" + orderId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), RestaurantDiscount.class);
    }

    public static Collection<RestaurantDiscount> getDiscountsApplicableToOrder(String orderId) throws IOException {
        // TODO: Add endpoint for this
        return getDiscountsForOrder(orderId).stream().filter(discount -> !discount.options().appliesAfterOrder()).toList();
    }
}
