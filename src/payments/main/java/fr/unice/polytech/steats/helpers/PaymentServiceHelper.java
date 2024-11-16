package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Payment;
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
 * Helper class for calling the payment service.
 *
 * @author Team C
 */
public class PaymentServiceHelper {
    public static final URI PAYMENT_SERVICE_URI = URI.create("http://localhost:5003/api/payments/");

    private PaymentServiceHelper() {
    }

    /**
     * Get a payment by its id.
     *
     * @param paymentId The id of the payment
     */
    public static Payment getPayment(String paymentId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(PAYMENT_SERVICE_URI.resolve(paymentId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Payment.class);
    }

    /**
     * Get all payments for an order.
     *
     * @param orderId The id of the order
     */
    public static List<Payment> getPaymentsForOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(PAYMENT_SERVICE_URI.resolve("?orderId=" + orderId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Payment.class);
    }

    /**
     * Get all payments made by a user.
     *
     * @param userId The id of the user
     */
    public static List<Payment> getPaymentsOfUser(String userId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(PAYMENT_SERVICE_URI.resolve("?userId=" + userId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Payment.class);
    }

    /**
     * Pay for an order.
     *
     * @param orderId The id of the order
     */
    public static Payment payForOrder(String orderId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(PAYMENT_SERVICE_URI.resolve("/pay"))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(Map.of("orderId", orderId))))
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Payment.class);
    }
}
