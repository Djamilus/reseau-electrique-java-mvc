package up.mi.paa.reseau_electrique.model;
/**
 * La classe Connexion représente l'association entre
 * une maison et un générateur dans le réseau électrique.
 * Chaque connexion relie exactement une maison à un générateur.
 */

import java.util.Objects;

public class Connexion {
private Maison maison;
private Generateur generateur;

public Connexion(Maison maison, Generateur generateur) {
	
	this.maison = maison;
	this.generateur = generateur;
}

public Maison getMaison() {
	return maison;
}
public void setMaison(Maison maison) {
	this.maison = maison;
}
public Generateur getGenerateur() {
	return generateur;
}
public void setGenerateur(Generateur generateur) {
	this.generateur = generateur;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Connexion c = (Connexion) o;
    return maison.equals(c.maison) && generateur.equals(c.generateur);
}

@Override
public int hashCode() {
    return Objects.hash(maison, generateur);
}


}
