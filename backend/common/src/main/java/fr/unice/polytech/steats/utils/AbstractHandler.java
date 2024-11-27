package fr.unice.polytech.steats.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.unice.polytech.steats.utils.openapi.ApiBodyParam;
import fr.unice.polytech.steats.utils.openapi.ApiPathParam;
import fr.unice.polytech.steats.utils.openapi.ApiQueryParam;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ConnectException;
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
                    handleMethodCall(method, p, HttpUtils.parseQuery(e.getRequestURI().getQuery()), e.getRequestBody()).send(e);
                } catch (InvocationTargetException ex) {
                    if (ex.getTargetException() instanceof IOException ioEx)
                        throw ioEx;
                    getLogger().log(Level.SEVERE, "Exception thrown by handler method", ex);
                    HttpUtils.sendJsonResponse(e, HttpUtils.INTERNAL_SERVER_ERROR_CODE, "Method threw an exception");
                } catch (IllegalAccessException ex) {
                    getLogger().log(Level.SEVERE, "Exception thrown while calling handler method", ex);
                    HttpUtils.sendJsonResponse(e, HttpUtils.INTERNAL_SERVER_ERROR_CODE, "Error calling method");
                }
            });
        }
    }

    private HttpResponse handleMethodCall(Method method, Map<String, String> uriParam, Map<String, String> queryParams, InputStream body) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[method.getParameterCount()];
        JsonNode bodyJson;
        try {
            bodyJson = JacksonUtils.getMapper().readTree(body);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Invalid JSON body", e);
            return new HttpResponse(HttpUtils.BAD_REQUEST_CODE, "Invalid JSON body");
        }

        int i = 0;
        for (Parameter arg : method.getParameters()) {
            if (arg.isAnnotationPresent(ApiBodyParam.class)) {
                ApiBodyParam bodyParam = arg.getAnnotation(ApiBodyParam.class);
                if (bodyJson == null && bodyParam.required()) {
                    getLogger().log(Level.SEVERE, "Missing required body");
                    return new HttpResponse(HttpUtils.BAD_REQUEST_CODE, "Missing required body");
                } else if (bodyJson != null) {
                    JsonNode node = bodyParam.name().isBlank() ? bodyJson : bodyJson.get(bodyParam.name());
                    if (node != null) args[i] = JacksonUtils.getMapper().convertValue(node, arg.getType());
                    else if (bodyParam.required()) {
                        getLogger().log(Level.SEVERE, "Missing required parameter");
                        return new HttpResponse(HttpUtils.BAD_REQUEST_CODE, "Missing required parameter");
                    }
                }
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
        } catch (ConnectException e) {
            getLogger().log(Level.WARNING, "Connection to sub-service failed", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_GATEWAY_CODE, "Connection to sub-service failed");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Exception thrown while handling request", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.INTERNAL_SERVER_ERROR_CODE, "An error occurred while processing the request");
        }
    }
}
