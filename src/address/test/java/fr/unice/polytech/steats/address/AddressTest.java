package fr.unice.polytech.steats.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {

    @Test
    void testAddress() {
        Address address = new Address("Campus Sophia Tech", "930 Route des Colles", "Valbonne", "06560", "Bâtiment 1");
        assertEquals("Campus Sophia Tech", address.label());
        assertEquals("930 Route des Colles", address.street());
        assertEquals("Valbonne", address.city());
        assertEquals("06560", address.postal_code());
        assertEquals("Bâtiment 1", address.additional_address());
    }
}
