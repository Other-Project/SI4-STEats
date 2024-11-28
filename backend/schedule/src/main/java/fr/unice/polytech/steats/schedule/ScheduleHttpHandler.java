package fr.unice.polytech.steats.schedule;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Schedules", path = "/api/schedules")
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
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }

    @ApiRoute(path = "/", method = HttpUtils.GET)
    private void getAll(HttpExchange exchange, Map<String, String> query) throws IOException {
        if (query.isEmpty()) {
            super.getAll(exchange);
            return;
        }
        String restaurantId = query.get("restaurantId");
        String startTime = query.get("startTime");
        String endTime = query.get("endTime");
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (startTime != null) {
            try {
                start = LocalDateTime.parse(startTime);
            } catch (Exception e) {
                HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, "Invalid start time");
                return;
            }
        }
        if (endTime != null) {
            try {
                end = LocalDateTime.parse(endTime);
            } catch (Exception e) {
                HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, "Invalid end time");
                return;
            }
        }
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getSchedule(restaurantId, start, end));
    }
}
