package fr.unice.polytech.steats.gateway;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;

public class GatewayHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/";
    public static final int API_PORT = 5000;

    protected GatewayHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        new GatewayHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        registerHandler(null, "/", new GatewayHttpHandler(API_ADDRESS, getLogger()));
    }
}
