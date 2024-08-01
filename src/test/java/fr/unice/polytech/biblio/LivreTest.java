package fr.unice.polytech.biblio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Ph. Collet
 */
class LivreTest { // Just pour vérifier que JUnit 5 est bien configuré

    private Livre livre;

    @BeforeEach
    public void setUp() {
        livre = new Livre("titre");
    }

    @Test
    void testGetTitre() {
        assertEquals("titre", livre.getTitre());
    }

    @Test
    void testConstructorAndEquals() {
        Livre livre = new Livre("titre");
        assertEquals("titre", livre.getTitre());
        Livre livre2 = new Livre("titre");
        assertEquals("titre", livre2.getTitre());
        assertNotEquals(livre, livre2);
    }

}
