package fr.unice.polytech.steats.gateway;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class GatewayHttpHandler extends AbstractHandler {
    public static final Map<String, String> SERVICES = Map.of(
            "/api/address", "localhost:5001",
            "/api/users", "localhost:5002",
            "/api/payments", "localhost:5003",
            "/api/restaurants", "localhost:5006",
            "/api/menu-items", "localhost:5007",
            "/api/schedules", "localhost:5008",
            "/api/orders", "localhost:5010"
    );
    public static final List<String> RESTRICTED_HEADERS = List.of(
            "host", "connection", "content-length", "transfer-encoding", "date", "server", "expect", "upgrade", "via", "warning"
    );

    protected GatewayHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    @Override
    protected void handle(HttpExchange exchange, String requestPath) throws IOException {
        String service = SERVICES.entrySet().stream()
                .filter(e -> requestPath.startsWith(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (service == null) {
            exchange.sendResponseHeaders(HttpUtils.NOT_FOUND_CODE, -1);
            exchange.close();
            return;
        }

        URI uri = exchange.getRequestURI();
        HttpRequest request;
        String[] headers = exchange.getRequestHeaders().entrySet().stream()
                .filter(header -> !RESTRICTED_HEADERS.contains(header.getKey().toLowerCase()))
                .flatMap(e -> e.getValue().stream().map(v -> Stream.of(e.getKey(), v)))
                .flatMap(Function.identity())
                .toArray(String[]::new);
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("http", service, uri.getPath(), uri.getQuery(), uri.getFragment()))
                    .headers(headers)
                    .method(exchange.getRequestMethod(),
                            exchange.getRequestBody() == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofInputStream(exchange::getRequestBody))
                    .build();
        } catch (URISyntaxException | IllegalArgumentException e) {
            getLogger().log(java.util.logging.Level.SEVERE, "Couldn't construct forwarded request", e);
            exchange.sendResponseHeaders(HttpUtils.INTERNAL_SERVER_ERROR_CODE, -1);
            exchange.close();
            return;
        }

        try {
            HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
            exchange.getResponseHeaders().putAll(response.headers().map());
            try (InputStream body = response.body()) {
                exchange.sendResponseHeaders(response.statusCode(), 0);
                exchange.getResponseBody().write(body.readAllBytes());
            }
            exchange.close();
        } catch (IOException e) {
            getLogger().log(java.util.logging.Level.SEVERE, "Error forwarding response", e);
            exchange.sendResponseHeaders(HttpUtils.BAD_GATEWAY_CODE, -1);
            exchange.close();
        }
    }
}
