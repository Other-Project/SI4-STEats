package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AbstractHttpServer {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());
    private final Map<String, String> registeredHandlers = new HashMap<>();
    private final HttpServer server;
    private final int apiPort;

    protected Logger getLogger() {
        return logger;
    }

    protected void registerHandler(String name, String subpath, HttpHandler handler) {
        logger.info(() -> "Registering " + name + " handler at " + subpath);
        server.createContext(subpath, handler);
        registeredHandlers.put(name, subpath);
    }

    protected void registerHandlers() {
        server.createContext("/", (exchange -> HttpUtils.sendJsonResponse(
                exchange,
                HttpUtils.OK_CODE,
                registeredHandlers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, kv -> "http://localhost:" + apiPort + kv.getValue()))
        )));
    }

    protected AbstractHttpServer(int apiPort) throws IOException {
        server = HttpServer.create(new InetSocketAddress(apiPort), 0);
        this.apiPort = apiPort;
        registerHandlers();
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        server.start();
        logger.info(() -> "Server started, accessible at http://localhost:" + apiPort);
    }
}
