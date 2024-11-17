package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Schedule;
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

public class ScheduleServiceHelper {
    public static final URI SCHEDULE_SERVICE_URI = URI.create("http://localhost:5008/api/schedule/");

    private ScheduleServiceHelper() {
    }

    public static List<Schedule> getScheduleByRestaurantId(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SCHEDULE_SERVICE_URI.resolve("restaurant/" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Schedule.class);
    }

    public static List<Schedule> getScheduleForDeliveryTime(String restaurantId, LocalDateTime deliveryTime, Duration maxPreparationTimeBeforeDelivery) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SCHEDULE_SERVICE_URI.resolve("delivery/toDeliver?restaurantId=" + restaurantId + "&deliveryTime=" + deliveryTime + "&maxPreparationTimeBeforeDelivery=" + maxPreparationTimeBeforeDelivery))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Schedule.class);
    }

}
