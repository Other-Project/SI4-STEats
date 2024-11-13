package fr.unice.polytech.steats.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe permet d'enregistrer et de récupérer un ensemble de routes.
 */
public class ApiRegistry {
    private static final List<RouteInfo> routes = new ArrayList<>();

    public static void registerRoute(String method, String path, RouteHandler handler) {
        routes.add(new RouteInfo(method, path, handler));
    }

    public static List<RouteInfo> getRoutes() {
        return routes;
    }
}
