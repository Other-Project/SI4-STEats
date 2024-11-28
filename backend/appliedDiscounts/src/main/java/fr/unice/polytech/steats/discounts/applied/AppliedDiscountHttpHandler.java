package fr.unice.polytech.steats.discounts.applied;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.models.AppliedDiscount;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

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
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath(), (exchange, params) -> create(exchange));
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/api/discounts/applied", summary = "Get all applied discounts")
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

    @ApiRoute(method = HttpUtils.POST, path = "/api/discounts/applied", summary = "Save a list of applied discounts")
    private void create(HttpExchange exchange) throws IOException {
        JsonNode json = JacksonUtils.getMapper().readTree(exchange.getRequestBody());

        String userId = json.get("userId").asText();
        String unlockOrderId = json.get("unlockOrderId").asText();
        ArrayNode discounts = (ArrayNode) json.get("discounts");

        if (userId == null || unlockOrderId == null || discounts == null) {
            HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, "Missing parameters");
            return;
        }

        AppliedDiscount[] discountList = new AppliedDiscount[discounts.size()];
        int i = 0;
        for (JsonNode discountNode : discounts) {
            if (discountNode.size() != 1) {
                HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, "Discounts should be a list of key-value pairs");
                return;
            }
            Map.Entry<String, JsonNode> discount = discountNode.fields().next();
            discountList[i++] = new AppliedDiscount(userId, unlockOrderId, discount.getKey(), discount.getValue().asText());
        }
        getManager().add(discountList);

        HttpUtils.sendJsonResponse(exchange, HttpUtils.CREATED_CODE, discountList);
    }
}
