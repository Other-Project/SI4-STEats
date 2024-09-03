package fr.unice.polytech.biblio;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * Ph. Collet
 */
public class BookRentalStepdefs {

    Bibliotheque biblio = new Bibliotheque();
    Etudiant etudiant;
    Livre livre;

    public BookRentalStepdefs() {} // implementation des steps dans le constructeur (aussi possible dans des méthodes)

    @Given("a student of name {string} and with student id {int}")
    public void givenAStudent(String nomEtudiant, Integer noEtudiant)  // besoin de refactorer int en Integer car utilisation de la généricité par Cucumber Java 8
    {
        etudiant = new Etudiant();
        etudiant.setNom(nomEtudiant);
        etudiant.setNoEtudiant(noEtudiant);
        biblio.addEtudiant(etudiant);
    }

    @And("a book of title {string}")
    public void andABook(String titreLivre)  {
        Livre liv = new Livre(titreLivre);
        biblio.addLivre(liv);
    }


    @Then("There is {int} in his number of rentals")
    public void thenNbRentals(Integer nbEmprunts) {
        assertEquals(nbEmprunts,etudiant.getNombreDEmprunts());
    }


    @When("{string} requests his number of rentals")
    public void whenRequestsRentals (String nomEtudiant) {
        etudiant = biblio.getEtudiantByName(nomEtudiant);
    }

    @When("{string} rents the book {string}")
    public void whenRenting(String nomEtudiant, String titreLivre)  {
        etudiant = biblio.getEtudiantByName(nomEtudiant);
         if (biblio.getLivreDisponibleByTitle(titreLivre).isPresent()) {
             livre = biblio.getLivreDisponibleByTitle(titreLivre).get();
             biblio.emprunte(etudiant, livre);
         }
    }

    @And("The book {string} is in a rental in the list of rentals")
    public void andNarrowedBook (String titreLivre){
        assertTrue(etudiant.getEmprunts().stream().
                anyMatch(emp -> emp.getLivreEmprunte().getTitre().equals(titreLivre)));
    }

    @And("The book {string} is unavailable")
    public void andUnvailableBook(String titreLivre) {
        assertTrue(biblio.getLivreDisponibleByTitle(titreLivre).isEmpty());
    }

    @Given("{string} has rent the book {string}")
    public void hasRentTheBook(String studentName, String bookTitle) {
        Etudiant e = biblio.getEtudiantByName(studentName);
        Livre book = biblio.getLivreDisponibleByTitle(bookTitle).orElse(null);
        if (book == null) {
            throw new IllegalStateException("Book not available" + bookTitle);
        }
        biblio.emprunte(e,book);
    }

    @When("{string} returns the book {string}")
    public void returnsTheBook(String studentName, String bookTitle) {
       Etudiant e = biblio.getEtudiantByName(studentName);
       if (e == null) {
           throw new IllegalStateException("Student not found" + studentName);
       }
       var emprunt = e.getEmpruntFor(bookTitle);
       if (emprunt == null) {
              throw new IllegalStateException("No rental found for " + bookTitle + " for " + studentName);
       }
       Livre book = emprunt.getLivreEmprunte();
       biblio.rend(book);
    }

    @And("The book {string} is available")
    public void theBookIsAvailable(String title) {
        assertTrue(biblio.getLivreDisponibleByTitle(title).isPresent());
    }
}
