package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;

import java.util.logging.Logger;

public class RestaurantHttpHandler extends AbstractManagerHandler<RestaurantManager, Restaurant> {
    public RestaurantHttpHandler(String subPath, Logger logger) {
        super(subPath, Restaurant.class, logger);
    }

    @Override
    protected RestaurantManager getManager() {
        return RestaurantManager.getInstance();
    }
}
