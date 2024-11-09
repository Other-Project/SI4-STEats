package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Order;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JaxsonUtils;

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

    public static Order getOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve(orderId))
                .header("accept", HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JaxsonUtils.fromJson(response.body(), Order.class);
    }
}
