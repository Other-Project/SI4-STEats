package fr.unice.polytech.steats.users;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class UserHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/users";
    public static final int API_PORT = 5002;

    protected UserHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--demo")) UserManager.getInstance().demo();
        new UserHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler(new UserHttpHandler(API_ADDRESS, getLogger()));
    }
}
