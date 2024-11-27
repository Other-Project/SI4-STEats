package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.steats.utils.openapi.ApiBodyParam;
import fr.unice.polytech.steats.utils.openapi.ApiPathParam;
import fr.unice.polytech.steats.utils.openapi.ApiQueryParam;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ApiRoute.class)) continue;
            ApiRoute route = method.getAnnotation(ApiRoute.class);
            ApiRegistry.registerRoute(route.method(), getSubPath() + route.path(), (e, p) -> {
                try {
                    handleMethodCall(method, p, HttpUtils.parseQuery(e.getRequestURI().getQuery())).send(e);
                } catch (Exception ex) {
                    getLogger().log(Level.SEVERE, "Exception thrown while handling request", ex);
                    e.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, 0);
                    e.close();
                }
            });
        }
    }

    private HttpResponse handleMethodCall(Method method, Map<String, String> uriParam, Map<String, String> queryParams) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[method.getParameterCount()];

        int i = 0;
        for (Parameter arg : method.getParameters()) {
            if (arg.isAnnotationPresent(ApiBodyParam.class)) {
                args[i] = null; // TODO
            } else if (arg.isAnnotationPresent(ApiPathParam.class))
                args[i] = uriParam.get(arg.getAnnotation(ApiPathParam.class).name());
            else if (arg.isAnnotationPresent(ApiQueryParam.class))
                args[i] = queryParams.get(arg.getAnnotation(ApiQueryParam.class).name());
            else {
                getLogger().log(Level.SEVERE, "Undeclared parameter type");
                return new HttpResponse(HttpUtils.INTERNAL_SERVER_ERROR_CODE, "Internal parameter declaration error");
            }
            i++;
        }
        if (!(method.invoke(this, args) instanceof HttpResponse response)) {
            getLogger().log(Level.SEVERE, "Method does not return an HttpResponse");
            return new HttpResponse(HttpUtils.INTERNAL_SERVER_ERROR_CODE, "Invalid method return type");
        }
        return response;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(HttpUtils.NO_CONTENT_CODE, -1);
            return;
        }

        String requestPath = exchange.getRequestURI().getPath().replaceAll("/$", "");
        getLogger().info(() -> "Received " + exchange.getRequestMethod() + " at " + requestPath);
        handle(exchange, requestPath);
    }

    protected void handle(HttpExchange exchange, String requestPath) throws IOException {
        Optional<RouteInfo> routeInfoOptional = ApiRegistry.getRoutes().stream()
                .filter(r -> r.matches(exchange.getRequestMethod(), requestPath))
                .findFirst();
        if (routeInfoOptional.isEmpty()) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
            exchange.close();
            return;
        }
        RouteInfo route = routeInfoOptional.get();
        Matcher matcher = route.getPathMatcher(requestPath);
        Map<String, String> params = matcher.find()
                ? matcher.namedGroups().keySet().stream().collect(Collectors.toMap(k -> k, matcher::group))
                : Map.of();
        try {
            route.getHandler().handle(exchange, params);
        } catch (IllegalArgumentException | IllegalStateException e) {
            getLogger().log(Level.WARNING, "Illegal request", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, e.getMessage());
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Exception thrown while handling request", e);
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, 0);
            exchange.close();
        }
    }
}
