package fr.unice.polytech.steats.menuitem;

import fr.unice.polytech.steats.utils.AbstractHttpServer;

import java.io.IOException;
import java.util.Arrays;

public class MenuItemHttpServer extends AbstractHttpServer {
    public static final String API_ADDRESS = "/api/menu-items";
    public static final int API_PORT = 5007;

    protected MenuItemHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("demo")) MenuItemManager.getInstance().demo();
        new MenuItemHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        registerHandler("menu-items", API_ADDRESS, new MenuItemHttpHandler(API_ADDRESS, getLogger()));
    }
}
