package up.mi.paa.reseau_electrique.model;

import java.util.List;
import java.util.Random;

public class Optimiseur {

    private static final Random rnd = new Random();

    /**
     * Optimise le réseau par une approche de type recuit simulé.
     *
     * elle prend en parametre le réseau à optimiser
     * elle retrourne le nombre d'itérations réellement effectuées
     */
    public static int optimiser(Reseau reseau) {

        // --- Cas triviaux : rien à optimiser ---
        if (reseau.getGenerateurs().size() < 2 || reseau.getMaisons().isEmpty()) {
            return 0;
        }

        double temperature = 1.0;
        double Tmin = 1e-4;
        double cooling = 0.9995;

        double coutActuel = reseau.coutTotal();

        Reseau best = copieReseau(reseau);
        double bestCost = coutActuel;

        List<Maison> maisons = reseau.getMaisons();
        List<Generateur> generateurs = reseau.getGenerateurs();

        int iterations = 0;
        int stagnation = 0;
        int maxStagnation = 10_000;

        while (temperature > Tmin && stagnation < maxStagnation) {

            iterations++;

            // Sécurité supplémentaire
            if (generateurs.size() <= 1) break;

            // Choix aléatoire d'une maison
            Maison m = maisons.get(rnd.nextInt(maisons.size()));

            // Générateur actuel de la maison
            Generateur ancienG = null;
            for (Connexion c : reseau.getConnexions()) {
                if (c.getMaison().equals(m)) {
                    ancienG = c.getGenerateur();
                    break;
                }
            }
            if (ancienG == null) continue;

            // Choix d'un nouveau générateur différent
            Generateur nouveauG;
            do {
                nouveauG = generateurs.get(rnd.nextInt(generateurs.size()));
            } while (nouveauG.equals(ancienG));

            // Modification temporaire
            reseau.modifierConnexion(m, nouveauG);
            double nouveauCout = reseau.coutTotal();

            double delta = nouveauCout - coutActuel;

            boolean accepter;
            if (delta < 0) {
                accepter = true;
            } else {
                double p = Math.exp(-delta / temperature);
                accepter = rnd.nextDouble() < p;
            }

            if (accepter) {
                coutActuel = nouveauCout;
            } else {
                // Annulation du changement
                reseau.modifierConnexion(m, ancienG);
            }

            // Mise à jour du meilleur réseau connu
            if (coutActuel < bestCost) {
                bestCost = coutActuel;
                best = copieReseau(reseau);
                stagnation = 0;
            } else {
                stagnation++;
            }

            temperature *= cooling;
        }

        // estaure la meilleure solution trouvée
        reseau.setConnexions(best.getConnexions());

        return iterations;
    }

   
     //Crée une copie d'un réseau.
     
    private static Reseau copieReseau(Reseau r) {
        Reseau copy = new Reseau();
        copy.setLambda(r.getLambda());

        for (Generateur g : r.getGenerateurs()) {
            copy.ajouterGenerateur(new Generateur(g.getNom(), g.getCapacite()));
        }

        for (Maison m : r.getMaisons()) {
            copy.ajouterMaison(new Maison(m.getNom(), m.getType()));
        }

        for (Connexion c : r.getConnexions()) {
            Generateur g = copy.recupererGenerateur(c.getGenerateur().getNom());
            Maison m = copy.recupererMaison(c.getMaison().getNom());
            copy.ajouterConnexion(new Connexion(m, g));
        }

        return copy;
    }
}
