package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;

import java.util.logging.Logger;

@ApiMasterRoute(name = "Restaurant discounts", path = "/api/discounts/restaurant")
public class RestaurantDiscountHttpHandler extends AbstractManagerHandler<RestaurantDiscountManager, Discount> {
    public RestaurantDiscountHttpHandler(String subPath, Logger logger) {
        super(subPath, Discount.class, logger);
    }

    @Override
    protected RestaurantDiscountManager getManager() {
        return RestaurantDiscountManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
    }
}
