package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractHttpServer;
import fr.unice.polytech.steats.utils.GatewayHttpHandler;

import java.io.IOException;
import java.net.URI;

public class OrderHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/orders";
    public static final int API_PORT = 5010;

    protected OrderHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        new OrderHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("orders", API_ADDRESS, new OrderHttpHandler(API_ADDRESS, getLogger()));
        registerHandler("singles", API_ADDRESS + "/singles", new GatewayHttpHandler(API_ADDRESS + "/singles", URI.create("http://localhost:5004"), getLogger()));
        registerHandler("groups", API_ADDRESS + "/groups", new GatewayHttpHandler(API_ADDRESS + "/groups", URI.create("http://localhost:5005"), getLogger()));
    }
}