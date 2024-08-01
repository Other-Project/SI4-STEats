package fr.unice.polytech.biblio;

import java.util.*;

/**
 * Ph. Collet
 * modifié par M. Blay-Fornarino
 */
public class Etudiant {

	private String nom;
	private int noEtudiant;
	// Les emprunts courants
	private Collection<Emprunt> emprunts = new ArrayList<>();

	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public int getNoEtudiant() {
		return this.noEtudiant;
	}

	public void setNoEtudiant(int noEtudiant) {
		this.noEtudiant = noEtudiant;
	}

	public Collection<Emprunt> getEmprunts() {
		return Collections.unmodifiableCollection(emprunts);
	}

	public int getNombreDEmprunts() {
		return emprunts.size();
	}
	public void addEmprunt(Emprunt emprunt) {
		emprunts.add(emprunt);
	}

	public void removeEmprunt(Emprunt emprunt) {
		emprunts.remove(emprunt);
	}

	//On considére qu'un seul exemplaire d'un livre peut etre emprunté par un étudiant
	public Emprunt getEmpruntFor(String livreTitre){
		return emprunts.stream().filter(e -> e.getLivreEmprunte().getTitre().equals(livreTitre)).findFirst().orElse(null);
	}

}