package fr.unice.polytech.steats.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * Interface servant à définir la fonction de callback qui va être fournie au moment de l'enregistrement de la Route.
 */
@FunctionalInterface
public interface RouteHandler {
    void handle(HttpExchange exchange, Map<String, String> params) throws IOException, NotFoundException;
}