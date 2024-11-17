package fr.unice.polytech.steats.schedule;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ScheduleHttpHandler extends AbstractManagerHandler<ScheduleManager, Schedule> {
    public ScheduleHttpHandler(String subPath, Logger logger) {
        super(subPath, Schedule.class, logger);
    }

    @Override
    protected ScheduleManager getManager() {
        return ScheduleManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "{id}/delivery", this::scheduleForDeliveryTime);
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    private void scheduleForDeliveryTime(HttpExchange exchange, Map<String, String> param) throws IOException {
        Map<String, Object> params = JacksonUtils.mapFromJson(exchange.getRequestBody());
        String restaurantId = params.get("id").toString();
        String deliveryTimeString = params.get("deliveryTime").toString();
        String maxPreparationTimeBeforeDeliveryString = params.get("maxPreparationTimeBeforeDelivery").toString();
        if (restaurantId == null || deliveryTimeString == null || maxPreparationTimeBeforeDeliveryString == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        LocalDateTime deliveryTime;
        Duration maxPreparationTimeBeforeDelivery;
        try {
            deliveryTime = LocalDateTime.parse(deliveryTimeString);
            maxPreparationTimeBeforeDelivery = Duration.parse(maxPreparationTimeBeforeDeliveryString);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            return;
        }
        List<Schedule> schedules = getManager().getScheduleByRestaurantId(restaurantId);
        List<Schedule> schedules2HoursBefore = schedules.stream()
                .filter(schedule -> schedule.isBetween(deliveryTime.minus(maxPreparationTimeBeforeDelivery), deliveryTime))
                .toList();
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, schedules2HoursBefore);
    }

    private void getAll(HttpExchange exchange, Map<String, String> query) throws IOException {
        if (query.isEmpty()) {
            super.getAll(exchange);
            return;
        }
        String restaurantId = query.get("restaurantId");
        if (restaurantId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getScheduleByRestaurantId(restaurantId));
    }
}
