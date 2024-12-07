package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

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
        registerHandler(new OrderHttpHandler(API_ADDRESS, getLogger()));
        registerGatewayHandler(API_ADDRESS + "/singles", URI.create("http://localhost:5004"), getLogger());
        registerGatewayHandler(API_ADDRESS + "/groups", URI.create("http://localhost:5005"), getLogger());
    }
}