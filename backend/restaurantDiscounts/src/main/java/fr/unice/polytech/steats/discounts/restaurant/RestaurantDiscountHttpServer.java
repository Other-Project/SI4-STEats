package fr.unice.polytech.steats.discounts.restaurant;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class RestaurantDiscountHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/discounts/restaurant";
    public static final int API_PORT = 5009;

    protected RestaurantDiscountHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) RestaurantDiscountManager.getInstance().demo();
        new RestaurantDiscountHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler(new RestaurantDiscountHttpHandler(API_ADDRESS, getLogger()));
    }
}
