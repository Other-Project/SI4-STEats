package fr.unice.polytech.steats.helpers;

//import fr.unice.polytech.steats.discount.Discount;

import fr.unice.polytech.steats.models.AppliedDiscount;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Helper class for calling the Discount service.
 *
 * @author Team C
 */
public class AppliedDiscountServiceHelper {

    public static final URI APPLIED_DISCOUNT_SERVICE_URI = URI.create("http://localhost:5011/api/discounts/applied/");

    private AppliedDiscountServiceHelper() {
    }


    /**
     * Get a discount by its id.
     *
     * @param discountId The id of the discount
     */
    public static AppliedDiscount getDiscount(String discountId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(APPLIED_DISCOUNT_SERVICE_URI.resolve(discountId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), AppliedDiscount.class);

    }

    /**
     * Get discounts applied to an order.
     *
     * @param orderId The id of the order
     */
    public static List<AppliedDiscount> getDiscountsAppliedToOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(APPLIED_DISCOUNT_SERVICE_URI.resolve("?appliedOrderId=" + orderId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), AppliedDiscount.class);
    }

    /**
     * Get discounts unlocked by previous orders of a user that have not been used yet.
     *
     * @param userId The id of the user
     */
    public static List<AppliedDiscount> getUnusedDiscountsOfUser(String userId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(APPLIED_DISCOUNT_SERVICE_URI.resolve("?appliedOrderId=&userId=" + userId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), AppliedDiscount.class);
    }

    /**
     * Save the discounts unlocked by an order.
     *
     * @param unlockOrderId The id of the order that unlocked the discounts
     * @param userId        The id of the user
     * @param discounts     The discounts to unlock (map of discount id and the order id to which it has been applied)
     */
    public static void unlockDiscounts(String unlockOrderId, String userId, List<Map.Entry<String, String>> discounts) throws IOException {
        // TODO : Create endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(APPLIED_DISCOUNT_SERVICE_URI)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("userId", userId, "unlockOrderId", unlockOrderId, "discounts", discounts))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        if (response.statusCode() != HttpUtils.OK_CODE) {
            throw new IOException("Failed to unlock discounts");
        }
    }
}
