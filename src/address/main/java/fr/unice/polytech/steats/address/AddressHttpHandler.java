package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.utils.AbstractManagerHandler;

public class AddressHttpHandler extends AbstractManagerHandler<AddressManager, Address> {
    public AddressHttpHandler(String subPath) {
        super(subPath, Address.class);

    }

    @Override
    protected AddressManager getManager() {
        return AddressManager.getInstance();
    }
}
