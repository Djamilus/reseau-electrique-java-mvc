package up.mi.paa.reseau_electrique.model;
/**
 * La classe Generateur représente un générateur électrique
 * avec un nom et une capacité en kilowatts.
 * Chaque générateur fournit une puissance limitée déterminée par sa capacité.
 */

public class Generateur {
private int capacite;
private String nom;
public Generateur(String nom,int capacite) {

    if (nom == null || nom.isBlank()){
        throw new IllegalArgumentException("Nom générateur invalide");
    }

    if (capacite <= 0) {
        throw new IllegalArgumentException("Capacité générateur doit être strictement positive");
    }

    this.capacite = capacite;
	this.nom = nom;
}
public int getCapacite() {
	return capacite;
}
public void setCapacite(int capacite) {
    if (capacite <= 0) {
        throw new IllegalArgumentException("Capacité générateur doit être strictement positive");
    }
    this.capacite = capacite;
}
public String getNom() {
	return nom;
}
public void setNom(String nom) {
	this.nom = nom;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Generateur g = (Generateur) o;
    return nom.equalsIgnoreCase(g.nom); 
}
    @Override
    public String toString() {
        return nom;
    }


@Override
public int hashCode() {
    return nom.toLowerCase().hashCode();
}

}
