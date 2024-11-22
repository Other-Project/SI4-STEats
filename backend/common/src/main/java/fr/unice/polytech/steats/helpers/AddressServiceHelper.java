package fr.unice.polytech.steats.helpers;

import fr.unice.polytech.steats.address.Address;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper class for calling the address service.
 *
 * @author Team C
 */
public class AddressServiceHelper {
    public static final URI ADDRESS_SERVICE_URI = URI.create("http://localhost:5001/api/address/");

    private AddressServiceHelper() {

    }

    /**
     * Get an address by its id.
     *
     * @param addressId The id of the address
     */
    public static Address getAddress(String addressId) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(ADDRESS_SERVICE_URI.resolve(addressId))
                .header(HttpUtils.ACCEPT, HttpUtils.APPLICATION_JSON)
                .GET()
                .build();
        HttpResponse<InputStream> response = HttpUtils.sendRequest(request);
        return JacksonUtils.fromJson(response.body(), Address.class);
    }

}
