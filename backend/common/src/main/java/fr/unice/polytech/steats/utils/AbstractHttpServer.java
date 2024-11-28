package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.OpenAPI;
import fr.unice.polytech.steats.utils.openapi.OpenAPIGenerator;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        server.createContext("/", (exchange -> HttpUtils.sendJsonResponse(
                exchange,
                HttpUtils.OK_CODE,
                registeredHandlers.entrySet().stream().collect(Collectors.toMap(kv -> kv.getValue().name(), kv -> "http://localhost:" + apiPort + kv.getValue().path()))
        )));
        server.createContext("/openapi.json", (exchange -> HttpUtils.sendJsonResponse(
                exchange,
                HttpUtils.OK_CODE,
                openApi
        )));
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
