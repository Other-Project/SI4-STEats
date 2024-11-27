package fr.unice.polytech.steats.gateway;

import fr.unice.polytech.steats.utils.AbstractHttpServer;
import fr.unice.polytech.steats.utils.GatewayHttpHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class GatewayHttpServer extends AbstractHttpServer {
    public static final int API_PORT = 5000;
    public static final Map<String, URI> API_SUB_SERVICES = Map.of(
            "/api/address", URI.create("http://localhost:5001"),
            "/api/users", URI.create("http://localhost:5002"),
            "/api/payments", URI.create("http://localhost:5003"),
            "/api/restaurants", URI.create("http://localhost:5006"),
            "/api/menu-items", URI.create("http://localhost:5007"),
            "/api/schedules", URI.create("http://localhost:5008"),
            "/api/discounts/restaurant", URI.create("http://localhost:5009"),
            "/api/orders", URI.create("http://localhost:5010"),
            "/api/discounts/applied", URI.create("http://localhost:5011")
    );

    protected GatewayHttpServer(int apiPort) throws IOException {
        super(apiPort);
    }

    public static void main(String[] args) throws IOException {
        new GatewayHttpServer(API_PORT).start();
    }

    @Override
    protected void registerHandlers() {
        super.registerHandlers();
        for (Map.Entry<String, URI> service : API_SUB_SERVICES.entrySet()) {
            String[] pathParts = service.getKey().split("/");
            registerHandler(pathParts[pathParts.length - 1], service.getKey(), new GatewayHttpHandler(service.getKey(), service.getValue(), getLogger()));
        }
    }
}
