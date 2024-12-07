package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.OpenAPI;
import fr.unice.polytech.steats.utils.openapi.OpenAPIGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public abstract class AbstractHttpServer {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());
    private final Map<HttpHandler, ApiMasterRoute> registeredHandlers = new HashMap<>();
    private final Map<URI, OpenAPI> proxiedHandlers = new HashMap<>();
    private final HttpServer server;
    private final int apiPort;
    private final OpenAPI openApi;

    protected Logger getLogger() {
        return logger;
    }

    protected void registerHandler(HttpHandler handler) {
        ApiMasterRoute handlerInfo = handler.getClass().getAnnotation(ApiMasterRoute.class);
        if (handlerInfo == null) throw new IllegalArgumentException("Handler " + handler.getClass().getName() + " is missing the @ApiMasterRoute annotation");
        logger.info(() -> "Registering " + handlerInfo.name() + " handler at " + handlerInfo.path());
        server.createContext(handlerInfo.path(), handler);
        registeredHandlers.put(handler, handlerInfo);
    }

    protected void registerGatewayHandler(String path, URI url, Logger logger) {
        HttpHandler handler = new GatewayHttpHandler(path, url, logger);
        logger.info(() -> "Registering gateway handler for " + url);
        server.createContext(path, handler);
        proxiedHandlers.put(url, null);
    }

    protected void registerHandlers() {
        server.createContext("/", exchange -> {
            switch (exchange.getRequestURI().getPath()) {
                case "/" -> {
                    exchange.getResponseHeaders().set("Location", "/openapi.json");
                    exchange.sendResponseHeaders(HttpUtils.MOVED_PERMANENTLY_CODE, -1);
                    exchange.close();
                }
                case "/openapi.json" -> HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, openApi.merge(getProxiedOpenAPIs()));
                case "/favicon.ico" -> {
                    try (InputStream faviconStream = getClass().getClassLoader().getResourceAsStream("favicon.ico")) {
                        if (faviconStream == null) {
                            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
                            exchange.close();
                            return;
                        }
                        exchange.sendResponseHeaders(HttpUtils.OK_CODE, 0);
                        faviconStream.transferTo(exchange.getResponseBody());
                        exchange.close();
                    }
                }
                default -> HttpUtils.sendJsonResponse(exchange, HttpUtils.NOT_FOUND_CODE, "Not found");
            }
        });
    }

    private Collection<OpenAPI> getProxiedOpenAPIs() {
        for (Map.Entry<URI, OpenAPI> kv : proxiedHandlers.entrySet()) {
            if (kv.getValue() != null) continue;
            try {
                HttpRequest request = HttpRequest.newBuilder().uri(kv.getKey().resolve("/openapi.json")).header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON).GET().build();
                proxiedHandlers.put(kv.getKey(), JacksonUtils.fromJson(HttpUtils.sendRequest(request).body(), OpenAPI.class));
            } catch (Exception e) {
                logger.warning("Failed to fetch OpenAPI documentation from " + kv.getKey());
            }
        }
        return proxiedHandlers.values();
    }

    protected AbstractHttpServer(int apiPort) throws IOException {
        server = HttpServer.create(new InetSocketAddress(apiPort), 0);
        this.apiPort = apiPort;
        registerHandlers();
        server.setExecutor(Executors.newCachedThreadPool());

        logger.info(() -> "Generating OpenAPI documentation");
        OpenAPI.Server openApiServer = new OpenAPI.Server("http://localhost:" + apiPort);
        Class<?>[] handlers = registeredHandlers.keySet().stream().map(HttpHandler::getClass).toArray(Class[]::new);
        openApi = OpenAPIGenerator.generate(openApiServer, handlers);
    }

    public void start() {
        server.start();
        logger.info(() -> "Server started, accessible at http://localhost:" + apiPort);
    }
}
