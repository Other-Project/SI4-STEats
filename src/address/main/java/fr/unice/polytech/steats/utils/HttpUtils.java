package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpUtils {
    private HttpUtils() {
    }

    public static final int OK_CODE = 200;
    public static final int CREATED_CODE = 201;
    public static final int NO_CONTENT_CODE = 204;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int NOT_FOUND_CODE = 404;
    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * Parse a query string into a map.
     *
     * @param query The query string
     * @return A map containing the parameters and their corresponding values
     */
    public static Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();
        return Stream.of(query.split("&"))
                .filter(s -> !s.isEmpty())
                .map(kv -> kv.split("=", 2))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
    }

    public static void sendJsonResponse(HttpExchange exchange, int code, Object object) throws IOException {
        exchange.getResponseHeaders().add(CONTENT_TYPE, APPLICATION_JSON);
        var json = JaxsonUtils.toJson(object).getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, json.length);
        exchange.getResponseBody().write(json);
        exchange.close();
    }
}
