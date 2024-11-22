package fr.unice.polytech.steats.address;

import fr.unice.polytech.steats.models.Address;
import fr.unice.polytech.steats.utils.AbstractManager;

/**
 * Manage addresses (store addresses, and allow to create, delete and get them)
 *
 * @author Team C
 */
@SuppressWarnings("java:S6548")
public class AddressManager extends AbstractManager<Address> {
    private static final AddressManager INSTANCE = new AddressManager();

    private AddressManager() {
        super();
    }

    /**
     * Get the instance of the {@link AddressManager}
     */
    public static AddressManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Address item) {
        add(item.label(), item);
    }

    /**
     * Fill the manager with some demo data
     */
    public void demo() {
        String biot = "Biot";
        String biotCP = "06410";
        add(new Address("EURECOM", "450 Route des Chappes", biot, biotCP, "Campus SophiaTech"));
        add(new Address("Campus Sophia Tech", "930 Route des Colles", biot, biotCP, "Bâtiment A"));
        add(new Address("IUT", "650 Rte des Colles", biot, biotCP, null));
        add(new Address("INRIA", "2004 Rte des Lucioles", biot, biotCP, null));
        add(new Address("I3S", "2000 Rte des Lucioles", biot, biotCP, "Euclide B"));
        add(new Address("Site des Lucioles", "1645 Rte des Lucioles", biot, biotCP, null));
    }
}
