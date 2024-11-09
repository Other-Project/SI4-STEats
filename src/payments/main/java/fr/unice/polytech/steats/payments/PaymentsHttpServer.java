package fr.unice.polytech.steats.payments;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;

public class PaymentsHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/payments";
    public static final int API_PORT = 5003;

    protected PaymentsHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        new PaymentsHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("payments", API_ADDRESS, new PaymentsHttpHandler(API_ADDRESS, getLogger()));
    }
}
