package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.OpenAPI;
import fr.unice.polytech.steats.utils.openapi.OpenAPIGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public abstract class AbstractHttpServer {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());
    private final Map<HttpHandler, ApiMasterRoute> registeredHandlers = new HashMap<>();
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

    /**
     * @deprecated Use {@link #registerHandler(HttpHandler)} instead
     */
    @Deprecated(forRemoval = true)
    protected void registerHandler(String name, String subpath, HttpHandler handler) {
        logger.info(() -> "Registering " + name + " handler at " + subpath);
        server.createContext(subpath, handler);
        registeredHandlers.put(handler, new ApiMasterRoute() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String path() {
                return subpath;
            }
        });
    }

    protected void registerHandlers() {
        server.createContext("/", exchange -> {
            switch (exchange.getRequestURI().getPath()) {
                case "/" -> {
                    exchange.getResponseHeaders().set("Location", "/openapi.json");
                    exchange.sendResponseHeaders(HttpUtils.MOVED_PERMANENTLY_CODE, -1);
                    exchange.close();
                }
                case "/openapi.json" -> HttpUtils.sendJsonResponse(exchange, HttpUtils.OK_CODE, openApi);
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
