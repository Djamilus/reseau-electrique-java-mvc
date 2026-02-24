package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class OptimiseurTest {

    @Test
    public void testOptimiserMaintientReseauValide() {

        Reseau r = new Reseau();
        r.setLambda(10);

        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Maison m2 = new Maison("M2", TypeMaison.MOYENNE_CONSOMMATION);
        Generateur g1 = new Generateur("G1", 50);
        Generateur g2 = new Generateur("G2", 50);

        r.ajouterMaison(m1);
        r.ajouterMaison(m2);
        r.ajouterGenerateur(g1);
        r.ajouterGenerateur(g2);

        r.ajouterConnexion(new Connexion(m1, g1));
        r.ajouterConnexion(new Connexion(m2, g2));

        // 🔹 Nouveau Optimiseur : pas d'itérations fixes, on récupère le nb réel
        int iterations = Optimiseur.optimiser(r);
        System.out.println("Optimisation terminée en " + iterations + " itérations.");

        // --- Vérifications ---

        assertEquals(2, r.getMaisons().size());
        assertEquals(2, r.getGenerateurs().size());

        // Chaque maison est connectée à un générateur
        for (Maison m : r.getMaisons()) {
            boolean connectee = false;
            for (Connexion c : r.getConnexions()) {
                if (c.getMaison().equals(m)) {
                    connectee = true;
                    break;
                }
            }
            assertTrue(connectee, "La maison " + m.getNom() + " doit être connectée");
        }

        // Chaque générateur des connexions existe dans le réseau
        List<Generateur> genList = r.getGenerateurs();
        for (Connexion c : r.getConnexions()) {
            assertTrue(genList.contains(c.getGenerateur()));
        }
    }
}
