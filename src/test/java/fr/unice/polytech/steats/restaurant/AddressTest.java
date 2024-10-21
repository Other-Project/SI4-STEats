package fr.unice.polytech.steats.restaurant;

import fr.unice.polytech.steats.order.Address;
import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void testAddress() {
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        assert (address.label().equals("Campus Sophia Tech"));
        assert (address.street().equals("930 Route des Colles"));
        assert (address.city().equals("Valbonne"));
        assert (address.postal_code().equals("06560"));
        assert (address.additional_address().equals("Bâtiment 1"));
    }
}
