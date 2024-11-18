package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractManagerHandler<T extends AbstractManager<U>, U> extends AbstractHandler {
    protected abstract T getManager();

    private final Class<U> clazz;

    protected AbstractManagerHandler(String subPath, Class<U> clazz, Logger logger) {
        super(subPath, logger);
        this.clazz = clazz;
    }

    @Override
    protected void register() {
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath() + "/{id}", this::get);
        ApiRegistry.registerRoute(HttpUtils.GET, getSubPath(), (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute(HttpUtils.PUT, getSubPath(), (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute(HttpUtils.DELETE, getSubPath() + "/{id}", this::remove);
    }

    protected void get(HttpExchange exchange, Map<String, String> params) throws IOException {
        try {
            U object = getManager().get(params.get("id"));
            HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, object);
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
            exchange.getResponseBody().close();
        }
    }

    protected void getAll(HttpExchange exchange) throws IOException {
        HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, getManager().getAll());
    }

    protected void add(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);
            U object = JacksonUtils.fromJson(exchange.getRequestBody(), clazz);
            getManager().add(object);
            exchange.sendResponseHeaders(HttpUtils.CREATED_CODE, -1);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, -1);
        }
        exchange.getResponseBody().close();
    }

    protected void remove(HttpExchange exchange, Map<String, String> params) throws IOException {
        try {
            getManager().remove(params.get("id"));
            exchange.sendResponseHeaders(HttpUtils.NO_CONTENT_CODE, -1);
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
        }
        exchange.getResponseBody().close();
    }
}
