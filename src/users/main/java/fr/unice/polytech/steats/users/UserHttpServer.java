package fr.unice.polytech.steats.users;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class UserHttpServer {
    public static final String API_ADDRESS = "/api/users";
    public static final int API_PORT = 5002;
    static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("UserHttpServer");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(API_PORT), 0);
        server.createContext(API_ADDRESS, new UserHttpHandler(API_ADDRESS, logger));
        server.setExecutor(null);
        server.start();
        logger.info("Server started, accessible at http://localhost:" + API_PORT + API_ADDRESS);
    }
}
