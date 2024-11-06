package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.utils.AbstractManager;

/**
 * Manage addresses (store addresses, and allow to create, delete and get them)
 *
 * @author Team C
 */
public class AddressManager extends AbstractManager<Address> {
    private static final AddressManager INSTANCE = new AddressManager();

    private AddressManager() {
        super();
    }

    /**
     * Get the instance of the {@link AddressManager}
     *
     * @return The instance of the {@link AddressManager}
     */
    public static AddressManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Address item) {
        add(item.label(), item);
    }
}
