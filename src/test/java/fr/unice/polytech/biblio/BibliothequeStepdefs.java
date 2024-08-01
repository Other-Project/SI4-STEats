package fr.unice.polytech.biblio;

import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BibliothequeStepdefs {


    Bibliotheque bibliotheque;

    @Etantdonné("une bibliothèque")
    public Bibliotheque uneBibliotheque() {
        this.bibliotheque = new Bibliotheque();
        return bibliotheque;
    }

    @Quand("le bibliothécaire  ajoute le livre {string}")
    public void leBibliothecaireAjouteLeLivre(String titreLivre) {
        Livre livre = new Livre(titreLivre);
        bibliotheque.addLivre(livre);
    }

    @Alors("la bibliothèque contient le livre {string} en un exemplaire")
    public void laBibliothequeContientLeLivreEnUnExemplaire(String title) {
        List<Livre> livres = bibliotheque.getLivresByTitle(title);
        assertEquals(1, livres.size());
        Optional<Livre> livre = bibliotheque.getLivreDisponibleByTitle(title);
        assertTrue(livre.isPresent());
        assertEquals(title, livre.get().getTitre());
    }


    @Quand("le bibliothécaire ajoute un étudiant {string} avec le numéro d'étudiant {int}")
    public void leBibliothecaireAjouteUnEtudiantAvecLeNumeroDEtudiant(String nom, int ident) {
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(nom);
        etudiant.setNoEtudiant(ident);
        bibliotheque.addEtudiant(etudiant);
    }

    @Alors("la bibliothèque contient un étudiant {string} avec le numéro d'étudiant {int}")
    public void laBibliothequeContientUnEtudiantAvecLeNumeroDEtudiant(String nom, int ident) {
        Etudiant etudiant = bibliotheque.getEtudiantByName(nom);
        assertEquals(nom, etudiant.getNom());
        assertEquals(ident, etudiant.getNoEtudiant());
    }


    @Quand("le bibliothécaire  ajoute deux exemplaires du livre {string}")
    public void leBibliothecaireAjouteDeuxExemplairesDuLivre(String titreLivre) {
        Livre livre = new Livre(titreLivre);
        bibliotheque.addLivre(livre);
        livre = new Livre(titreLivre);
        bibliotheque.addLivre(livre);
    }

    @Alors("la bibliothèque contient deux exemplaires du livre {string}")
    public void laBibliothequeContientDeuxExemplairesDuLivre(String titre) {
        List<Livre> livres = bibliotheque.getLivresByTitle(titre);
        assertEquals(2, livres.size());
    }
}
