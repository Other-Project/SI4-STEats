package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.models.Address;
import fr.unice.polytech.steats.utils.AbstractHandler;
import fr.unice.polytech.steats.utils.HttpResponse;
import fr.unice.polytech.steats.utils.HttpUtils;
import fr.unice.polytech.steats.utils.NotFoundException;
import fr.unice.polytech.steats.utils.openapi.ApiBodyParam;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;
import fr.unice.polytech.steats.utils.openapi.ApiPathParam;
import fr.unice.polytech.steats.utils.openapi.ApiRoute;

import java.util.List;
import java.util.logging.Logger;

@ApiMasterRoute(name = "Addresses", path = "/api/address")
public class AddressHttpHandler extends AbstractHandler {
    public AddressHttpHandler(String subPath, Logger logger) {
        super(subPath, logger);
    }

    private AddressManager getManager() {
        return AddressManager.getInstance();
    }

    @ApiRoute(method = HttpUtils.GET, path = "", description = "Get all addresses")
    public List<Address> getAll() {
        return getManager().getAll();
    }

    @ApiRoute(method = HttpUtils.PUT, path = "", description = "Create a new address")
    public HttpResponse create(@ApiBodyParam Address address) {
        getManager().add(address);
        return new HttpResponse(HttpUtils.CREATED_CODE);
    }

    @ApiRoute(method = HttpUtils.GET, path = "/{id}", description = "Get an address by its id")
    public Address get(@ApiPathParam(name = "id", description = "ID of the address") String id) throws NotFoundException {
        return getManager().get(id);
    }

    @ApiRoute(method = HttpUtils.DELETE, path = "/{id}", description = "Remove an address by its id")
    public void remove(@ApiPathParam(name = "id", description = "ID of the address to remove") String id) throws NotFoundException {
        getManager().remove(id);
    }
}
