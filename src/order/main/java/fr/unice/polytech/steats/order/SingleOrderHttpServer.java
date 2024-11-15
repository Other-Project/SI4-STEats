package fr.unice.polytech.steats.order;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class SingleOrderHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/orders/singles";
    public static final int API_PORT = 5004;

    protected SingleOrderHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) SingleOrderManager.getInstance().demo();
        new SingleOrderHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("singles", API_ADDRESS, new SingleOrderHttpHandler(API_ADDRESS, getLogger()));
    }
}
