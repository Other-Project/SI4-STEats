package fr.unice.polytech.steats.order.groups;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class GroupOrderHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/orders/groups";
    public static final int API_PORT = 5005;

    protected GroupOrderHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) GroupOrderManager.getInstance().demo();
        new GroupOrderHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler(new GroupOrderHttpHandler(API_ADDRESS, getLogger()));
    }
}
