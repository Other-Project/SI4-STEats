package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class AddressHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/address";
    public static final int API_PORT = 5001;

    protected AddressHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) AddressManager.getInstance().demo();
        new AddressHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler(new AddressHttpHandler(API_ADDRESS, getLogger()));
    }
}
