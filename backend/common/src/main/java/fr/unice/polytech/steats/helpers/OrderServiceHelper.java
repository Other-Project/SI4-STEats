package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Order;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;
import fr.unice.polytech.steats.models.Status;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class OrderServiceHelper {
    public static final URI ORDER_SERVICE_URI = URI.create("http://localhost:5010/api/orders/");

    private OrderServiceHelper() {
    }

    public static List<Order> getOrderByRestaurant(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder().uri(ORDER_SERVICE_URI.resolve("?restaurantId=" + restaurantId)).header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON).GET().build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Order.class);
    }

    public static List<Order> getOrderPastStatus(String restaurantId, Status status, LocalDateTime start, LocalDateTime end) throws IOException {
        //Todo : call the service instead of filtering in the helper
        List<Order> orders = getOrderByRestaurant(restaurantId);
        return orders.stream()
                .filter(order -> order.status().compareTo(Status.PAID) > 0 || (order.status() == status && order.deliveryTime() != null))
                .filter(order -> !start.isAfter(order.deliveryTime()) && end.isAfter(order.deliveryTime()))
                .toList();
    }
}
