package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {
    private final Map<String, String> headers;
    private final int statusCode;
    private final String message;

    public HttpResponse(int statusCode, String message, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.message = message;
        this.headers = headers;
    }

    public HttpResponse(int statusCode, String message) {
        this(statusCode, message, Map.of());
    }

    public HttpResponse(int statusCode) {
        this(statusCode, null);
    }

    @Override
    public String toString() {
        return statusCode + ": " + message;
    }

    /**
     * Send the response
     *
     * @param exchange The exchange to send the response to
     */
    public void send(HttpExchange exchange) throws IOException {
        byte[] data = message == null ? new byte[0] : message.getBytes(StandardCharsets.UTF_8);
        headers.forEach((headerKey, headerValue) -> exchange.getResponseHeaders().add(headerKey, headerValue));
        exchange.sendResponseHeaders(statusCode, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }
}
