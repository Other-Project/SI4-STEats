package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;
import fr.unice.polytech.steats.utils.openapi.ApiMasterRoute;

import java.util.logging.Logger;

@ApiMasterRoute(name = "Addresses", path = "/api/address")
public class AddressHttpHandler extends AbstractManagerHandler<AddressManager, Address> {
    public AddressHttpHandler(String subPath, Logger logger) {
        super(subPath, Address.class, logger);
    }

    @Override
    protected AddressManager getManager() {
        return AddressManager.getInstance();
    }
}
