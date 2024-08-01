package fr.unice.polytech.biblio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class EtudiantTest {

    Etudiant e1 ;
    Etudiant e2 ;

    @BeforeEach
    void setUp() {
        e1 = new Etudiant();
        e1.setNom("Marcel");
        //Use of underscore to improve readability
        e1.setNoEtudiant(123_456);
        e2 = new Etudiant();
        e2.setNom("Walid");
        e2.setNoEtudiant(6_789);
    }

    private void initEmprunts() {
        Livre livre = new Livre("Plus jamais sans moi");
        //Un autre exemplaire du même livre
        Livre livre1 = new Livre("Plus jamais sans moi");
        e1.addEmprunt(new Emprunt(LocalDate.now(),e1,livre));
        e1.addEmprunt(new Emprunt(LocalDate.now(),e1,livre1));
        e2.addEmprunt(new Emprunt(LocalDate.now(),e2,livre));
    }
    @Test
    void testGetNombreDEmprunts() {
        assertEquals(0,e1.getNombreDEmprunts());
        initEmprunts();
        assertEquals(1,e2.getNombreDEmprunts());
        assertEquals(2,e1.getNombreDEmprunts());
    }

    @Test
    void testAddEmprunt() {
        Livre livre = new Livre("Plus jamais sans moi");
        e1.addEmprunt(new Emprunt(LocalDate.now(),e1,livre));
        Collection<Emprunt> emprunts = e1.getEmprunts();
        assertEquals(1,emprunts.size());
        e1.addEmprunt(new Emprunt(LocalDate.now(),e1,new Livre("Plus jamais sans moi")));
        emprunts = e1.getEmprunts();
        assertEquals(2,emprunts.size());
    }

    @Test
    void testRemoveEmprunt() {
        initEmprunts();
        Collection<Emprunt> emprunts = e1.getEmprunts();
        assertEquals(2,emprunts.size());
        Emprunt emprunt = emprunts.iterator().next();
        e1.removeEmprunt(emprunt);
        assertEquals(1,emprunts.size());
        assertFalse(emprunts.contains(emprunt));

    }

    @Test
    void testGetEmpruntFor() {
        initEmprunts();
        Emprunt emprunt = e1.getEmpruntFor("Plus jamais sans moi");
        assertNotNull(emprunt);
        assertEquals("Plus jamais sans moi",emprunt.getLivreEmprunte().getTitre());
        assertNull(e1.getEmpruntFor("Le livre que je n'ai pas emprunté"));
    }
}