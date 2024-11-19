package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class AbstractHandler implements HttpHandler {
    private final String subPath;
    private final Logger logger;

    protected AbstractHandler(String subPath, Logger logger) {
        this.subPath = subPath;
        this.logger = logger;
        register();
    }

    public String getSubPath() {
        return subPath;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void register() {

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath().replaceAll("/$", "");

        getLogger().info(() -> "Received " + requestMethod + " at " + requestPath);
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
        try {
            route.getHandler().handle(exchange, params);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Exception thrown while handling request", e);
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, 0);
            exchange.getResponseBody().close();
        }
    }
}
