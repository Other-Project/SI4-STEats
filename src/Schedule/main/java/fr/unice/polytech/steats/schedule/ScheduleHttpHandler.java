package fr.unice.polytech.steats.schedule;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;

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
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/restaurant/{restaurantId}", (exchange, param) -> getScheduleByRestaurantId(exchange, param, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/delivery/toDeliver", (exchange, param) -> scheduleForDeliveryTime(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    private void scheduleForDeliveryTime(HttpExchange exchange, Map<String, String> query) throws IOException {
        String restaurantId = query.get("restaurantId");
        String deliveryTimeString = query.get("deliveryTime");
        String maxPreparationTimeBeforeDeliveryString = query.get("maxPreparationTimeBeforeDelivery");
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

    private void getScheduleByRestaurantId(HttpExchange exchange, Map<String, String> param, Map<String, String> query) throws IOException {
        String restaurantId = param.get("restaurantId");
        if (restaurantId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
            exchange.close();
            return;
        }
        List<Schedule> schedules = getManager().getScheduleByRestaurantId(restaurantId);
        String weekday = query.get("dayOfWeek");
        if (weekday != null)
            schedules = schedules.stream().filter(schedule -> schedule.getDayOfWeek().name().equalsIgnoreCase(weekday)).toList();
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, schedules);
    }
}
