package fr.unice.polytech.biblio;

import java.time.LocalDate;
import java.util.*;

/**
 * Ph. Collet
 * modifié par M. Blay-Fornarino
 * Cette classe joue le rôle de Facade
 */
public class Bibliotheque {

	public static final int DUREE_MAX_EMPRUNT = 15;
	private Map<String,Etudiant> etudiants = new HashMap<>();
	private Map<String,List<Livre>> livres = new HashMap<>();
	private Map<Livre,Emprunt> emprunts = new HashMap<>();

	/********** Gestion des étudiants **********/
	public Etudiant getEtudiantByName(String nom) {
		return etudiants.get(nom);
	}

	public void addEtudiant(Etudiant e) {
		etudiants.put(e.getNom(),e);
	}

   /************* Gestion des livres *******************/
	public void addLivre(Livre l) {
		livres.putIfAbsent(l.getTitre(), new ArrayList<>());
		livres.get(l.getTitre()).add(l);
	}



	/********** Gestion des emprunts de livres **********/
	public Optional<Livre> getLivreDisponibleByTitle(String titre) {
		if ( (livres.get(titre) == null) || (livres.get(titre).isEmpty()) ) {
			return Optional.empty();
		}
		for (Livre l : livres.get(titre)) {
			if (!l.estEmprunte()) {
				return Optional.of(l);
			}
		}
		return Optional.empty();
	}

	/********** Gestion des emprunts de livres **********/
	public List<Livre> getLivresByTitle(String titre) {
		if ( (livres.get(titre) == null) || (livres.get(titre).isEmpty()) ) {
			return new ArrayList<>();
		}
		return livres.get(titre);
	}



	public boolean emprunte(Etudiant e, Livre l) {
		if (l.estEmprunte()) {
			return false;
		}
		Emprunt emprunt = new Emprunt(LocalDate.now().plusDays(DUREE_MAX_EMPRUNT), e, l);
		emprunts.put(l,emprunt);
		l.setEstEmprunte(true);
		e.addEmprunt(emprunt);

		return true;
	}

	public Emprunt getEmpruntByLivre(Livre l) {
		return emprunts.get(l);
	}

	//Cette méthode viole la loi de Demeter car elle connait trop de choses sur l'étudiant...
	protected boolean rend(Livre l) {
		if (!l.estEmprunte()) {
			return false;
		}
		Emprunt emprunt = emprunts.remove(l);
		l.setEstEmprunte(false);
		emprunt.getEmprunteur().removeEmprunt(emprunt);
		return true;
	}


}