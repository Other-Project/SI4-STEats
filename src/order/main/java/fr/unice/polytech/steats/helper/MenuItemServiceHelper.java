package fr.unice.polytech.steats.helper;

import fr.unice.polytech.steats.restaurant.MenuItem;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper class for calling the MenuItem service.
 *
 * @author Team C
 */
public class MenuItemServiceHelper {

    public static final URI ORDER_SERVICE_URI = URI.create("http://localhost:5005/api/orders");

    private MenuItemServiceHelper() {
    }

    /**
     * Get a menu item by its id.
     *
     * @param menuItemId The id of the menu item
     */
    public static MenuItem getMenuItem(String menuItemId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ORDER_SERVICE_URI.resolve(menuItemId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), MenuItem.class);
    }
}
