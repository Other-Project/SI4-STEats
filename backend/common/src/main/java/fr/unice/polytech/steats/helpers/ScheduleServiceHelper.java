package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.Schedule;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ScheduleServiceHelper {
    public static final URI SCHEDULE_SERVICE_URI = URI.create("http://localhost:5008/api/schedules/");

    private ScheduleServiceHelper() {
    }

    public static List<Schedule> getScheduleByRestaurantId(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SCHEDULE_SERVICE_URI.resolve("?restaurantId=" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Schedule.class);
    }

    public static List<Schedule> getScheduleByRestaurantIdAndWeekday(String restaurantId, DayOfWeek dayOfWeek) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SCHEDULE_SERVICE_URI.resolve("?restaurantId=" + restaurantId + "&dayOfWeek=" + dayOfWeek))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Schedule.class);
    }

    public static List<Schedule> getScheduleForDeliveryTime(String restaurantId, LocalDateTime deliveryTime, Duration maxPreparationTimeBeforeDelivery) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SCHEDULE_SERVICE_URI.resolve("?restaurantId=" + restaurantId + "&startTime=" + deliveryTime.minus(maxPreparationTimeBeforeDelivery) + "&endTime=" + deliveryTime))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), Schedule.class);
    }

    public static void addSchedule(Schedule schedule) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(SCHEDULE_SERVICE_URI)
                .header(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(JacksonUtils.toJson(schedule)))
                .build();
        HttpUtils.sendRequest(request);
    }
}
