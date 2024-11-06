package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;

public abstract class AbstractManagerHandler<T extends AbstractManager<U>, U> implements HttpHandler {
    protected abstract T getManager();

    private String subPath;

    private Class<U> clazz;

    protected AbstractManagerHandler(String subPath, Class<U> clazz) {
        this.subPath = subPath;
        this.clazz = clazz;
        register();
    }

    public void register() {
        ApiRegistry.registerRoute("GET", subPath + "/{objectID}", this::get);
        ApiRegistry.registerRoute("GET", subPath + "/", (exchange, param) -> getAll(exchange));
        ApiRegistry.registerRoute("PUT", subPath + "/", (exchange, param) -> add(exchange));
        ApiRegistry.registerRoute("DELETE", subPath + "/{objectID}", this::remove);
    }

    private void get(HttpExchange httpExchange, String s) throws IOException {
        try {
            U object = getManager().get(s);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
            JaxsonUtils.toJsonStream(object, httpExchange.getResponseBody());

        } catch (NotFoundException e) {
            httpExchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
            httpExchange.getResponseBody().close();
        }

    }

    public void remove(HttpExchange exchange, String objectID) throws IOException {
        try {
            getManager().remove(objectID);
            exchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
        } catch (NotFoundException e) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, 0);
        }
        exchange.getResponseBody().close();
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

    public void getAll(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
        JaxsonUtils.toJsonStream(getManager().getAll(), exchange.getResponseBody());
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Remplacez par votre origine cliente
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Accept, X-Requested-With, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization");

        String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();

        //logger.log(java.util.logging.Level.INFO, "MembersHandler called: " + requestMethod + " " + requestPath);

        // La requête ne va pas être traitée directement ici mais via une des fonctions de callback définies dans le constructeur, il s'agit juste de trouver la bonne en
        // fonction du chemin et de la méthode.
        // Note : pour l'instant on ne traite que la possibilité d'avoir un seul paramètre dans le chemin, mais on pourrait permettre d'en avoir plusieurs assez simplement.
        Optional<RouteInfo> routeInfoOptional = ApiRegistry.getRoutes().stream().filter(r -> r.matches(requestMethod, requestPath)).findFirst();
        if (routeInfoOptional.isEmpty()) {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
            return;
        }
        RouteInfo route = routeInfoOptional.get();
        Matcher matcher = route.getPathMatcher(requestPath);
        String param = "";
        if (matcher.find() && matcher.groupCount() > 0) param = matcher.group(1);
        route.getHandler().handle(exchange, param);
    }
}
