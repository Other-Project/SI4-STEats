package fr.unice.polytech.biblio;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Ph. Collet
 */
public class Emprunt {

	private LocalDate dateDeRetourMax;
	private Livre livreEmprunte;
	private Etudiant emprunteur;

	public Emprunt(LocalDate d, Etudiant e, Livre l) {
		dateDeRetourMax = d;
		emprunteur = e;
		livreEmprunte = l;
	}

	public Livre getLivreEmprunte() {
		return livreEmprunte;
	}

	public Etudiant getEmprunteur() {
		return emprunteur;
	}

	public LocalDate getDateDeRetourMax() {
		return dateDeRetourMax;
	}

	/*
	On considère que deux emprunts sont égaux
	si le livre emprunté et l'emprunteur sont les mêmes
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Emprunt emprunt = (Emprunt) o;
		return Objects.equals(livreEmprunte, emprunt.livreEmprunte) && Objects.equals(emprunteur, emprunt.emprunteur);
	}

	@Override
	public int hashCode() {
		return Objects.hash(livreEmprunte, emprunteur);
	}
}