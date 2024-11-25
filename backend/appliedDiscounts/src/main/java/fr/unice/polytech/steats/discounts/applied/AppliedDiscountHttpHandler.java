package fr.unice.polytech.steats.discounts.applied;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.models.AppliedDiscount;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;

import java.io.IOException;
import java.util.Map;
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
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, params) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
    }

    private void getAll(HttpExchange exchange, Map<String, String> query) throws IOException {
        String appliedOrderId = query.get("appliedOrderId");
        String userId = query.get("userId");

        if (userId == null && appliedOrderId == null)
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
        else if (appliedOrderId == null)
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAppliedDiscountsOfUser(userId));
        else {
            if (appliedOrderId.isBlank()) appliedOrderId = null;
            if (userId != null)
                HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAppliedDiscountsOfOrderAndUser(appliedOrderId, userId));
            else
                HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAppliedDiscountsOfOrder(appliedOrderId));
        }
    }
}
