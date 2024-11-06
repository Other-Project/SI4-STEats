package fr.unice.polytech.steats.address;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AddressHttpServer {
    public static final String API_ADDRESS = "/api/address";
    public static final int API_PORT = 5001;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(API_PORT), 0);
        server.createContext(API_ADDRESS, new AddressHttpHandler(API_ADDRESS));
        server.setExecutor(null);
        server.start();
    }
}
