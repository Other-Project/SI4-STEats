package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpResponse;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Schedules", path = "/api/schedules")
public class ScheduleHttpHandler extends AbstractHandler {
    public ScheduleHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private ScheduleManager getManager() {
        return ScheduleManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "", description = "Get all schedules")
    public List<Schedule> getAll(
            @ApiQueryParam(name = "restaurantId", description = "ID of the restaurant concerned by the schedule") String restaurantId,
            @ApiQueryParam(name = "startTime", description = "Start date") LocalDateTime startTime,
            @ApiQueryParam(name = "endTime", description = "End date") LocalDateTime endTime
    ) {
        if (restaurantId == null && startTime == null && endTime == null) return getManager().getAll();
        return getManager().getSchedule(restaurantId, startTime, endTime);
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", description = "Create a new schedule", successStatus = HttpUtils.CREATED_CODE)
    public HttpResponse create(@ApiBodyParam Schedule schedule) {
        getManager().add(schedule);
        return new HttpResponse(HttpUtils.CREATED_CODE);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", description = "Get a schedule by its id")
    public Schedule get(@ApiPathParam(name = "id", description = "ID of the schedule") String id) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.DELETE, path = "/{id}", description = "Remove a schedule by its id")
    public void remove(@ApiPathParam(name = "id", description = "ID of the schedule to remove") String id) throws NotFoundException {
        getManager().remove(id);
    }
}
