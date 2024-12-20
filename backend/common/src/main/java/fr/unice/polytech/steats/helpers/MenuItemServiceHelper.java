package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.MenuItem;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MenuItemServiceHelper {
    public static final URI MENU_ITEM_SERVICE_URI = URI.create("http://localhost:5007/api/menu-items/");

    private MenuItemServiceHelper() {

    }

    public static MenuItem getMenuItem(String menuItemId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(MENU_ITEM_SERVICE_URI.resolve(menuItemId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), MenuItem.class);
    }

    public static MenuItem getAllMenuItem() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(MENU_ITEM_SERVICE_URI)
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), MenuItem.class);
    }

    public static List<MenuItem> getMenuItemByRestaurantId(String restaurantId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(MENU_ITEM_SERVICE_URI.resolve("?restaurantId=" + restaurantId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.listFromJson(response.body(), MenuItem.class);
    }
}
