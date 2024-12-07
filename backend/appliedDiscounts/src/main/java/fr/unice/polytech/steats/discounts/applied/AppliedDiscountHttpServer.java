package fr.unice.polytech.steats.discounts.applied;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class AppliedDiscountHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/discounts/applied";
    public static final int API_PORT = 5011;

    protected AppliedDiscountHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) AppliedDiscountManager.getInstance().demo();
        new AppliedDiscountHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler(new AppliedDiscountHttpHandler(API_ADDRESS, getLogger()));
    }
}
