package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Order;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper class for calling the order service.
 *
 * @author Team C
 */
public class OrderServiceHelper {
    public static final URI ORDER_SERVICE_URI = URI.create("http://localhost:5004/api/orders");

    private OrderServiceHelper() {
    }

    /**
     * Get an order by its id.
     *
     * @param orderId The id of the order
     */
    public static Order getOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve(orderId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Order.class);
    }
}
