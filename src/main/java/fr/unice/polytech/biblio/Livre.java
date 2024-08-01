package fr.unice.polytech.biblio;

import java.util.Arrays;
import java.util.Objects;

/**
 * Ph. Collet
 * Modifié par: M. Blay-Fornarino
 */
public class Livre {

	private String titre;
	private String[] auteurs;
	private String isbn;
	private String identifiant;
	private boolean estEmprunte;

	private static int compteur = 0;

	public Livre(String titre, String[] auteurs, String isbn) {
		this.titre = titre;
		this.auteurs = Arrays.copyOf(auteurs, auteurs.length);
		this.isbn = isbn;
		estEmprunte = false;
		identifiant = titre + "-" + compteur;
		compteur++;
	}

	public Livre(String titreLivre) {
		this(titreLivre, new String[0], null);
	}


	public String getTitre() {
		return this.titre;
	}

	/**
	 * 
	 * @param titre : titre du livre
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getIsbn() {
		return this.isbn;
	}

	/**
	 * 
	 * @param isbn
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String[] getAuteurs() {
		return Arrays.copyOf(this.auteurs, this.auteurs.length);
	}

	/**
	 * 
	 * @param auteurs
	 */
	public void setAuteurs(String... auteurs) {
		if (auteurs == null) {
			this.auteurs = new String[0];
			return;
		}
		this.auteurs = Arrays.copyOf(auteurs, auteurs.length);
	}

	public boolean estEmprunte() {
		return this.estEmprunte;
	}

	/**
	 * 
	 * @param estEmprunte
	 */
	public void setEstEmprunte(boolean estEmprunte) {
		this.estEmprunte = estEmprunte;
	}

	public String getIdentifiant() {
		return this.identifiant;
	}


	/**
	 * Deux livres sont égaux si ils ont le même identifiant,
	 * c'est à dire le même titre et le même compteur,
	 * donc qu'il s'agit du même exemplaire.
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Livre livre = (Livre) o;
		return Objects.equals(identifiant, livre.identifiant);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(identifiant);
	}

	@Override
	public String toString() {
		return "Livre{" +
				"'" + titre + '\'' +
				", de " + auteurs +
				", isbn='" + isbn + '\'' +
				", identifiant='" + identifiant + '\'' +
				", estEmprunte=" + estEmprunte +
				'}';
	}
}