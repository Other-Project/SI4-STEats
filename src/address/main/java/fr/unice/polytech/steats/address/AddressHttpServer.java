package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;

public class AddressHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/address";
    public static final int API_PORT = 5001;

    protected AddressHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        new AddressHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("address", API_ADDRESS, new AddressHttpHandler(API_ADDRESS, getLogger()));
    }
}
