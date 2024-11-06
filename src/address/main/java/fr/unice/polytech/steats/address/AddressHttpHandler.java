package fr.unice.polytech.steats.address;

import com.sun.net.httpserver.HttpExchange;
import fr.unice.polytech.steats.utils.AbstractManagerHandler;

import java.io.IOException;

public class AddressHttpHandler extends AbstractManagerHandler<AddressManager, Address> {


    public AddressHttpHandler(String subPath) {
        super(subPath,Address.class);

    }

    @Override
    protected AddressManager getManager() {
        return AddressManager.getInstance();
    }
}
