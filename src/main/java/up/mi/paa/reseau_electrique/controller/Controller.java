package up.mi.paa.reseau_electrique.controller;
/**
 * La classe Controller agit comme un intermédiaire entre l'interface utilisateur
 * et le modèle elle fournit les méthodes pour :
 * Valider les formats d'ajout pour maisons, générateurs et connexions
 * Ajouter, modifier ou supprimer des maisons et générateurs
 * Ajouter, modifier ou supprimer des connexions entre maisons et générateur
 * Vérifier l'intégrité des connexions (maisons reliées à un seul générateur)
 * Cette classe facilite la manipulation du modèle depuis l'interface graphique ou textuelle
 */

import java.util.ArrayList;
import java.util.List;

import up.mi.paa.reseau_electrique.model.Connexion;
import up.mi.paa.reseau_electrique.model.Generateur;
import up.mi.paa.reseau_electrique.model.Maison;
import up.mi.paa.reseau_electrique.model.TypeMaison;
import up.mi.paa.reseau_electrique.model.Reseau;

public class Controller {
private Reseau reseau;

	public Controller(Reseau reseau) {

	this.reseau = reseau;
}
	public static boolean typeMaisonValide(String type) {
		if(type.equalsIgnoreCase("BASSE") ||type.equalsIgnoreCase("NORMAL") ||type.equalsIgnoreCase("FORTE") ||type.equalsIgnoreCase("MOYENNE"))
			return true;
		else
			return false;
	}
	public static boolean formatAjoutMaison(String nouvM) {
	    if (nouvM == null) 
	    	return false;

	    
	    String[] parties = nouvM.trim().split("\\s+");

	    
	    if (parties.length != 2) 
	    	return false;

	  
	    String type = parties[1].toUpperCase();

	    
	    return typeMaisonValide(type);
	}


    public static boolean formatAjoutGenerateur(String nouvG) {
        if (nouvG == null)
            return false;

        String[] parties = nouvG.trim().split("\\s+");

        if (parties.length != 2)
            return false;

        try {
            int capacite = Integer.parseInt(parties[1]);
            return capacite > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean formatAjoutConnexion(String nouvC) {
		  if (nouvC == null) 
		    	return false;

		    
		    String[] parties = nouvC.trim().split("\\s+");

		    
		    if (parties.length != 2) 
		    	return false;
		    else
		    	return true;

	}
    public boolean ajouterMaison(String saisie) {
        if (!formatAjoutMaison(saisie)) return false;

        String[] parts = saisie.trim().split("\\s+");
        Maison maison = new Maison(parts[0], TypeMaison.stringToTypeMaison(parts[1]));
        if(reseau.maisonExistante(parts[0])) {
        	reseau.recupererMaison(parts[0]).setType(TypeMaison.stringToTypeMaison(parts[1]));
        }else {
        	reseau.ajouterMaison(maison);
        }
        
        return true;
    }

    public boolean ajouterGenerateur(String saisie) {
        if (!formatAjoutGenerateur(saisie)) {
            return false;
        }

        String[] parts = saisie.trim().split("\\s+");
        try {
            int cap = Integer.parseInt(parts[1]);

            if (reseau.generateurExistant(parts[0])) {
                reseau.recupererGenerateur(parts[0]).setCapacite(cap);
            } else {
                reseau.ajouterGenerateur(new Generateur(parts[0], cap));
            }
            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public boolean ajouterConnexion(String saisie, List<Connexion> connexions) {
        if (!formatAjoutConnexion(saisie)) return false;

        String[] parts = saisie.trim().split("\\s+");
        if (parts.length != 2) return false;
        
        Generateur g = reseau.recupererGenerateur(parts[0]);
        Maison m = reseau.recupererMaison(parts[1]);
        
       
        if (g == null || m == null) {
            g = reseau.recupererGenerateur(parts[1]);
            m = reseau.recupererMaison(parts[0]);
        }

       
        if (g == null || m == null) return false;
        final Maison maisonFinale = m;
        connexions.removeIf(c -> c.getMaison().equals(maisonFinale));

        connexions.add(new Connexion(m, g));
        return true;
    }

    public List<Maison> verifierConnexions(List<Connexion> connexions) {
        List<Maison> doublons = new ArrayList<>();

        for (Connexion c : connexions) {
            int count = 0;
            for (Connexion c1 : connexions) {
                if (c.getMaison().equals(c1.getMaison())) count++;
            }
            if (count > 1 && !doublons.contains(c.getMaison())) {
                doublons.add(c.getMaison());
            }
        }
        return doublons; 
    }


    public boolean ajouterConnexionsRx(List<Connexion> connexions) {
    	if(!verifierConnexions(connexions).isEmpty())
    		return false;
    	
    	this.reseau.setConnexions(connexions);
    	return true;
    	
    }
    public boolean modifierConnexion(String ancienne, String nouvelle) {

       
        if (!reseau.connexionExistante(ancienne))
            return false;

        String[] parts = nouvelle.trim().split("\\s+");
        if (parts.length != 2) return false;

        String p1 = parts[0];
        String p2 = parts[1];

        Maison maison = null;
        Generateur generateur = null;

      
        if (reseau.maisonExistante(p1) && reseau.generateurExistant(p2)) {
            maison = reseau.recupererMaison(p1);
            generateur = reseau.recupererGenerateur(p2);
        } else if (reseau.maisonExistante(p2) && reseau.generateurExistant(p1)) {
            maison = reseau.recupererMaison(p2);
            generateur = reseau.recupererGenerateur(p1);
        } else {
            return false;
        }

        return reseau.modifierConnexion(maison, generateur);
    }
    
    public boolean supprimerConnexion(String saisie, List<Connexion> connexions) {

        String[] parts = saisie.trim().split("\\s+");
        if (parts.length != 2) return false;

        Maison m ;
        Generateur g ;

        if (reseau.maisonExistante(parts[0]) && reseau.generateurExistant(parts[1])) {
            m = reseau.recupererMaison(parts[0]);
            g = reseau.recupererGenerateur(parts[1]);
        } else if (reseau.generateurExistant(parts[0]) && reseau.maisonExistante(parts[1])) {
            m = reseau.recupererMaison(parts[1]);
            g = reseau.recupererGenerateur(parts[0]);
        } else {
            return false;
        }

        return connexions.removeIf(c ->
            c.getMaison().equals(m) &&
            c.getGenerateur().equals(g)
        );
    }
    public boolean garderUnSeulGenerateur(Maison maison, Generateur garder, List<Connexion> connexions) {
        return connexions.removeIf(c ->
            c.getMaison().equals(maison) &&
            !c.getGenerateur().equals(garder)
        );
    }


}
