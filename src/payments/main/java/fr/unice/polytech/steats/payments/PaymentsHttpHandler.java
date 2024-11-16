package fr.unice.polytech.steats.payments;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.models.Payment;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class PaymentsHttpHandler extends AbstractManagerHandler<PaymentManager, Payment> {
    public PaymentsHttpHandler(String subPath, Logger logger) {
        super(subPath, Payment.class, logger);
    }

    @Override
    protected PaymentManager getManager() {
        return PaymentManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", super::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
        ApiRegistry.registerRoute(HttpUtils.POST, getSubPath() + "/pay", (exchange, param) -> pay(exchange));
    }

    private void getAll(HttpExchange exchange, Map<String, String> params) throws IOException {
        String orderId;
        String userId;
        if ((orderId = params.get("orderId")) != null)
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getPaymentsForOrder(orderId));
        else if ((userId = params.get("userId")) != null)
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getPaymentsOfUser(userId));
        else HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
    }

    private void pay(HttpExchange exchange) throws IOException {
        Map<String, Object> params = JacksonUtils.mapFromJson(exchange.getRequestBody());
        String orderId = params == null ? null : params.get("orderId").toString();
        if (orderId == null) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, 0);
            exchange.getResponseBody().close();
            return;
        }

        Optional<Payment> result = PaymentSystem.pay(orderId);
        if (result.isEmpty()) {
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, 0);
            exchange.getResponseBody().close();
        } else HttpUtils.sendJsonResponse(exchange, HttpUtils.CREATED_CODE, result.get());
    }
}
