package up.mi.paa.reseau_electrique.model;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe Reseau modélise l’ensemble du réseau électrique
 * avec ses maisons, ses générateurs et leurs connexions.
 * Elle fournit aussi les calculs de charge, de coût et de validation du réseau.
 */
public class Reseau {

    private List<Maison> maisons;
    private List<Generateur> generateurs;
    private List<Connexion> connexions;

    // paramètre lambda pour le coût
    private int lambda = 10;

    public Reseau() {
        maisons = new ArrayList<>();
        generateurs = new ArrayList<>();
        connexions = new ArrayList<>();
    }

    // ====== Lambda (coût) ======

    public void setLambda(int lambda) {
        if (lambda <= 0) lambda = 10;
        this.lambda = lambda;
    }

    public int getLambda() {
        return lambda;
    }

    // ====== Gestion maisons / générateurs / connexions ======

    public void ajouterMaison(Maison m) {
        maisons.add(m);
    }

    public void ajouterGenerateur(Generateur g) {
        generateurs.add(g);
    }

    public void ajouterConnexion(Connexion c) {
        
        connexions.add(c);
    }

    public List<Connexion> getConnexions() {
        return connexions;
    }

    public void setConnexions(List<Connexion> newConns) {
        this.connexions.clear();
        for (Connexion c : newConns) {
            Maison m = this.recupererMaison(c.getMaison().getNom());
            Generateur g = this.recupererGenerateur(c.getGenerateur().getNom());
            this.connexions.add(new Connexion(m, g));
        }
    }

    public List<Maison> getMaisons() {
        return maisons;
    }

    public List<Generateur> getGenerateurs() {
        return generateurs;
    }

    public boolean generateurExistant(String g) {
        for (Generateur i : generateurs) {
            if (i.getNom().equalsIgnoreCase(g))
                return true;
        }
        return false;
    }

    public boolean maisonExistante(String m) {
        for (Maison i : maisons) {
            if (i.getNom().equalsIgnoreCase(m))
                return true;
        }
        return false;
    }

    // vérifie si une connexion existe dans le réseau à partir d'une chaîne "X Y"
    public boolean connexionExistante(String c) {
        String[] parties = c.trim().split("\\s+");
        if (parties.length != 2) return false;

        String p1 = parties[0];
        String p2 = parties[1];

        if (!generateurExistant(p1) && !generateurExistant(p2)) return false;
        if (!maisonExistante(p1) && !maisonExistante(p2)) return false;

        for (Connexion i : connexions) {
            String nomG = i.getGenerateur().getNom();
            String nomM = i.getMaison().getNom();
            if ((nomG.equalsIgnoreCase(p1) && nomM.equalsIgnoreCase(p2)) ||
                    (nomG.equalsIgnoreCase(p2) && nomM.equalsIgnoreCase(p1))) {
                return true;
            }
        }
        return false;
    }

    // récupère une maison par son nom
    public Maison recupererMaison(String nom) {
        for (Maison m : maisons) {
            if (m.getNom().equalsIgnoreCase(nom))
                return m;
        }
        return null;
    }

    // récupère un générateur par son nom
    public Generateur recupererGenerateur(String nom) {
        for (Generateur g : generateurs) {
            if (g.getNom().equalsIgnoreCase(nom))
                return g;
        }
        return null;
    }

    // ====== AFFICHAGE DU RÉSEAU (version "jolie") ======

    public void affihcerRx() {
        System.out.println("\n==================================================");
        System.out.println("                ÉTAT DU RÉSEAU ÉLECTRIQUE         ");
        System.out.println("==================================================");

        // MAISONS
        System.out.println("\n--- Maisons --------------------------------------");
        if (maisons.isEmpty()) {
            System.out.println("  (aucune maison)");
        } else {
            for (Maison m : maisons) {
                System.out.printf("  %-12s | Type : %-22s | Conso : %5.1f kW\n",
                        m.getNom(),
                        m.getType() != null ? m.getType().name() : "INDEFINI",
                        m.getConsommation());
            }
        }

        // GÉNÉRATEURS
        System.out.println("\n--- Générateurs ----------------------------------");
        if (generateurs.isEmpty()) {
            System.out.println("  (aucun générateur)");
        } else {
            for (Generateur g : generateurs) {
                double charge = chargeGenerateur(g);
                double taux = tauxUtilisation(g) * 100;
                System.out.printf(
                        "  %-12s | Capacité : %4d kW | Charge : %5.1f kW | Utilisation : %6.2f%%\n",
                        g.getNom(), g.getCapacite(), charge, taux
                );
            }
        }

        // CONNEXIONS
        System.out.println("\n--- Connexions (Maison -> Générateur) ------------");
        if (connexions.isEmpty()) {
            System.out.println("  (aucune connexion)");
        } else {
            for (Connexion c : connexions) {
                Maison m = c.getMaison();
                Generateur g = c.getGenerateur();
                System.out.printf(
                        "  %-10s (%4.0f kW)  -->  %-10s\n",
                        m.getNom(), m.getConsommation(), g.getNom()
                );
            }
        }

        System.out.println("==================================================\n");
    }

    // ====== Charges et coûts ======

    // charge d'un générateur (somme des consommations de ses maisons)
    public double chargeGenerateur(Generateur g) {
        double total = 0.0;
        for (Connexion c : connexions) {
            if (c.getGenerateur().equals(g)) {
                total += c.getMaison().getConsommation();
            }
        }
        return total;
    }

    // taux d'utilisation d'un générateur
    public double tauxUtilisation(Generateur g) {
        int Cg = g.getCapacite();
        if (Cg <= 0) return 0.0;
        double Lg = chargeGenerateur(g);
        return Lg / Cg;
    }

    // moyenne des taux d'utilisation
    public double moyenneTauxUtilisation() {
        if (generateurs.isEmpty()) {
            return 0.0;
        }
        double somme = 0.0;
        for (Generateur g : generateurs) {
            somme += tauxUtilisation(g);
        }
        return somme / generateurs.size();
    }

    // dispersion
    public double dispersion() {
        double ubar = moyenneTauxUtilisation();
        double disp = 0.0;
        for (Generateur g : generateurs) {
            disp += Math.abs(tauxUtilisation(g) - ubar);
        }
        return disp;
    }

    // surcharge
    public double surcharge() {
        double s = 0.0;
        for (Generateur g : generateurs) {
            double Lg = chargeGenerateur(g);
            double Cg = g.getCapacite();
            if (Cg > 0) {
                s += Math.max(0.0, (Lg - Cg) / Cg);
            }
        }
        return s;
    }

    // coût total (avec lambda)
    public double coutTotal() {
        return dispersion() + lambda * surcharge();
    }

    // Modifie une connexion (maison -> nouveau générateur)
    public boolean modifierConnexion(Maison maison, Generateur nouveauGen) {
        if (maison == null || nouveauGen == null) return false;

        connexions.removeIf(c -> c.getMaison().equals(maison));
        connexions.add(new Connexion(maison, nouveauGen));

        return true;
    }

    // Vérifie que toutes les maisons sont connectées à au moins un générateur
    public boolean toutesMaisonsConnectees(List<Connexion> cons) {
        for (Maison m : maisons) {
            boolean connectee = false;
            for (Connexion c : cons) {
                if (c.getMaison().equals(m)) {
                    connectee = true;
                    break;
                }
            }
            if (!connectee) {
                return false;
            }
        }
        return true;
    }

    // Offre >= demande ? (utilisé dans ReseauIO)
    public boolean offreSuperieureOuEgaleADemande() {
        double totalConsommation = 0.0;
        for (Maison m : maisons) {
            totalConsommation += m.getConsommation();
        }

        double totalCapacite = 0.0;
        for (Generateur g : generateurs) {
            totalCapacite += g.getCapacite();
        }

        return totalCapacite >= totalConsommation;
    }

    // Version ancienne : retourne true si demande > capacité (utile si View l'appelle encore)
    public boolean offreSuperieureADemande() {
        double totalConsommation = 0.0;
        for (Maison m : maisons) {
            totalConsommation += m.getConsommation();
        }

        double totalCapacite = 0.0;
        for (Generateur g : generateurs) {
            totalCapacite += g.getCapacite();
        }

        return totalConsommation > totalCapacite;
    }
    
    
}
