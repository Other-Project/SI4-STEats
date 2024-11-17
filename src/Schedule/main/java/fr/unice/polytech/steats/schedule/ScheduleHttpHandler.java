package fr.unice.polytech.steats.schedule;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;

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
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", super::remove);
    }
}
