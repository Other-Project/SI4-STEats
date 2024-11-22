package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.models.User;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper class for calling the user service.
 *
 * @author Team C
 */
public class UserServiceHelper {
    public static final URI User_SERVICE_URI = URI.create("http://localhost:5002/api/address/");

    private UserServiceHelper() {

    }

    /**
     * Get an address by its id.
     *
     * @param userId The id of the address
     */
    public static User getUser(String userId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(User_SERVICE_URI.resolve(userId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), User.class);
    }

}
