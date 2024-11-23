package fr.unice.polytech.steats.discounts.applied;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;

import java.util.logging.Logger;

@ApiMasterRoute(name = "Applied discounts", path = "/api/discounts/applied")
public class AppliedDiscountHttpHandler extends AbstractManagerHandler<AppliedDiscountManager, AppliedDiscount> {
    public AppliedDiscountHttpHandler(String subPath, Logger logger) {
        super(subPath, AppliedDiscount.class, logger);
    }

    @Override
    protected AppliedDiscountManager getManager() {
        return AppliedDiscountManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
    }
}
