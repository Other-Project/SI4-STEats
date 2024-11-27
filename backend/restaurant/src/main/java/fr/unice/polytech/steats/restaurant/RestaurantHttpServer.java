package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class RestaurantHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/restaurants";
    public static final int API_PORT = 5006;

    protected RestaurantHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) RestaurantManager.getInstance().demo();
        new RestaurantHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler(new RestaurantHttpHandler(API_ADDRESS, getLogger()));
    }
}
