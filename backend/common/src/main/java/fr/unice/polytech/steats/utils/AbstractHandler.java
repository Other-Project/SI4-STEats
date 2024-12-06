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
                    switch (ex.getTargetException()) {
                        case IOException ioEx -> throw ioEx;
                        case NotFoundException notFoundEx -> throw notFoundEx;
                        case RuntimeException runEx -> throw runEx;
                        case null, default -> throw new IllegalDeclarationException("Method threw an exception", ex);
                    }
                } catch (IllegalAccessException ex) {
                    throw new IllegalDeclarationException("Method call failed", ex);
                }
            });
        }
    }

    private HttpResponse handleMethodCall(Method method, Map<String, String> uriParam, Map<String, String> queryParams, InputStream body) throws InvocationTargetException, IllegalAccessException, IOException {
        JsonNode bodyJson;
        try {
            bodyJson = JacksonUtils.getMapper().readTree(body);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid JSON body");
        }
        Object[] args = new Object[method.getParameterCount()];

        int i = 0;
        for (Parameter arg : method.getParameters()) args[i++] = getArgument(arg, uriParam, queryParams, bodyJson);
        Object returnValue = method.invoke(this, args);
        if (returnValue == null) return new HttpResponse(HttpUtils.NO_CONTENT_CODE);
        if (returnValue instanceof HttpResponse response) return response;
        return new JsonResponse<>(returnValue);
    }

    private Object getArgument(Parameter arg, Map<String, String> uriParam, Map<String, String> queryParams, JsonNode bodyJson) throws IOException {
        if (arg.isAnnotationPresent(ApiBodyParam.class)) {
            ApiBodyParam bodyParam = arg.getAnnotation(ApiBodyParam.class);
            if (bodyJson == null && bodyParam.required()) throw new IllegalArgumentException("Missing required body");
            if (bodyJson == null) return null;
            JsonNode node = bodyParam.name().isBlank() ? bodyJson : bodyJson.get(bodyParam.name());
            if (node != null) return JacksonUtils.getMapper().convertValue(node, arg.getType());
            if (bodyParam.required())
                throw new IllegalArgumentException("Missing required body parameter " + bodyParam.name());
            return null;
        } else if (arg.isAnnotationPresent(ApiPathParam.class))
            return readStringAsClass(uriParam.get(arg.getAnnotation(ApiPathParam.class).name()), arg.getType());
        else if (arg.isAnnotationPresent(ApiQueryParam.class))
            return readStringAsClass(queryParams.get(arg.getAnnotation(ApiQueryParam.class).name()), arg.getType());
        else throw new IllegalDeclarationException("Undeclared parameter type (" + arg + ")");
    }

    private <T> T readStringAsClass(String text, Class<T> tClass) throws IOException {
        if (text == null || text.isBlank()) return null;
        return JacksonUtils.fromJson("\"" + text + "\"", tClass);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(HttpUtils.NO_CONTENT_CODE, -1);
            exchange.close();
            return;
        }

        String requestPath = exchange.getRequestURI().getPath().replaceAll("/$", "");
        getLogger().info(() -> "Received " + exchange.getRequestMethod() + " at " + requestPath);

        try {
            handle(exchange, requestPath);
        } catch (IllegalArgumentException | IllegalStateException e) {
            getLogger().log(Level.WARNING, "Illegal request", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_REQUEST_CODE, e.getMessage());
        } catch (NotFoundException e) {
            getLogger().log(Level.INFO, "Not found exception", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, e.getMessage());
        } catch (ConnectException e) {
            getLogger().log(Level.WARNING, "Connection to sub-service failed", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.BAD_GATEWAY_CODE, "Connection to sub-service failed");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Exception thrown while handling request", e);
            HttpUtils.sendJsonResponse(exchange, HttpUtils.INTERNAL_SERVER_ERROR_CODE, "An error occurred while processing the request");
        }
    }

    protected void handle(HttpExchange exchange, String requestPath) throws IOException, NotFoundException {
        Optional<RouteInfo> routeInfoOptional = ApiRegistry.getRoutes().stream()
                .filter(r -> r.matches(exchange.getRequestMethod(), requestPath))
                .findFirst();
        if (routeInfoOptional.isEmpty())
            throw new NotFoundException("No route found for " + exchange.getRequestMethod() + " " + requestPath);
        RouteInfo route = routeInfoOptional.get();
        Matcher matcher = route.getPathMatcher(requestPath);
        Map<String, String> params = matcher.find()
                ? matcher.namedGroups().keySet().stream().collect(Collectors.toMap(k -> k, matcher::group))
                : Map.of();
        route.getHandler().handle(exchange, params);
    }

    private static class IllegalDeclarationException extends RuntimeException {
        public IllegalDeclarationException(String message) {
            super(message);
        }

        public IllegalDeclarationException(String message, Exception e) {
            super(message, e);
        }
    }
}
