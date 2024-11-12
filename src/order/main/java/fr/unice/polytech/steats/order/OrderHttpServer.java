package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class OrderHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/orders";
    public static final int API_PORT = 5004;

    protected OrderHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) SingleOrderManager.getInstance().demo();
        new OrderHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("orders", API_ADDRESS, new OrderHttpHandler(API_ADDRESS, getLogger()));
    }
}