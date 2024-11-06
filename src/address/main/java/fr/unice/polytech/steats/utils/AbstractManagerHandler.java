package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public abstract class AbstractManagerHandler<T extends AbstractManager<U>, U> implements HttpHandler {
    protected abstract T getManager();

    private final String subPath;
    private final Class<U> clazz;
    private final Logger logger;

    protected AbstractManagerHandler(String subPath, Class<U> clazz, Logger logger) {
        this.subPath = subPath;
        this.clazz = clazz;
        this.logger = logger;
        register();
    }

    public void register() {
        ApiRegistry.registerRoute("GET", subPath + "/{id}", this::get);
        ApiRegistry.registerRoute("GET", subPath, (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute("PUT", subPath, (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute("DELETE", subPath + "/{id}", this::remove);
    }

    private void get(HttpExchange httpExchange, Map<String, String> params) throws IOException {
        try {
            U object = getManager().get(params.get("id"));
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
            JaxsonUtils.toJsonStream(object, httpExchange.getResponseBody());

        } catch (NotFoundException e) {
            httpExchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
            httpExchange.getResponseBody().close();
        }
    }

    public void getAll(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
        JaxsonUtils.toJsonStream(getManager().getAll(), exchange.getResponseBody());
    }

    public void add(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            U object = JaxsonUtils.fromJson(exchange.getRequestBody(), clazz);
            getManager().add(object);
            exchange.sendResponseHeaders(HttpUtils.CREATED_CODE, 0);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpUtils.BAD_REQUEST_CODE, 0);
        }
        exchange.getResponseBody().close();
    }

    public void remove(HttpExchange exchange, Map<String, String> params) throws IOException {
        try {
            getManager().remove(params.get("id"));
            exchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
        }
        exchange.getResponseBody().close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath().replaceAll("/$", "");

        logger.info("Received " + requestMethod + " at " + requestPath);
        Optional<RouteInfo> routeInfoOptional = ApiRegistry.getRoutes().stream().filter(r -> r.matches(requestMethod, requestPath)).findFirst();
        if (routeInfoOptional.isEmpty()) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
            exchange.getResponseBody().close();
            return;
        }
        RouteInfo route = routeInfoOptional.get();
        Matcher matcher = route.getPathMatcher(requestPath);
        Map<String, String> params = matcher.find()
                ? matcher.namedGroups().keySet().stream().collect(Collectors.toMap(k -> k, matcher::group))
                : Map.of();
        route.getHandler().handle(exchange, params);
    }
}
