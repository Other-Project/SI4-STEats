package fr.unice.polytech.steats.payments;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.users.User;
import fr.unice.polytech.steats.users.UserManager;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.ApiRegistry;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JaxsonUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class PaymentsHttpHandler extends AbstractManagerHandler<UserManager, User> {
    public PaymentsHttpHandler(String subPath, Logger logger) {
        super(subPath, User.class, logger);
    }

    @Override
    protected UserManager getManager() {
        return UserManager.getInstance();
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute("GET", getSubPath() + "/{id}", this::get);
        ApiRegistry.registerRoute("GET", getSubPath(), (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute("PUSH", getSubPath() + "/pay", (exchange, param) -> pay(exchange, HttpUtils.parseQuery(exchange.getRequestURI().getQuery())));
    }

    private void pay(HttpExchange exchange, Map<String, String> params) throws IOException {
        String orderId = params.get("orderId");
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
