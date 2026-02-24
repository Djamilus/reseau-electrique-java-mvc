package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class ReseauTest {

    
    @Test
    public void testAjoutMaisonEtGenerateur() {
        Reseau r = new Reseau();

        Maison m = new Maison("Maison1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g = new Generateur("Gen1", 100);

        r.ajouterMaison(m);
        r.ajouterGenerateur(g);

        assertEquals(1, r.getMaisons().size());
        assertEquals(1, r.getGenerateurs().size());
    }

    

    @Test
    public void testMaisonExistante() {
        Reseau r = new Reseau();
        r.ajouterMaison(new Maison("MaisonA", TypeMaison.BASSE_CONSOMMATION));

        assertTrue(r.maisonExistante("MaisonA"));
        assertTrue(r.maisonExistante("maisona"));
        assertFalse(r.maisonExistante("MaisonB"));
    }

    @Test
    public void testGenerateurExistant() {
        Reseau r = new Reseau();
        r.ajouterGenerateur(new Generateur("GenA", 100));

        assertTrue(r.generateurExistant("GenA"));
        assertTrue(r.generateurExistant("gena"));
        assertFalse(r.generateurExistant("GenB"));
    }

    // ====== Connexions ======

    @Test
    public void testAjouterConnexion() {
        Reseau r = new Reseau();

        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g = new Generateur("G1", 100);

        r.ajouterMaison(m);
        r.ajouterGenerateur(g);
        r.ajouterConnexion(new Connexion(m, g));

        assertEquals(1, r.getConnexions().size());
    }

    @Test
    public void testConnexionExistante() {
        Reseau r = new Reseau();

        Maison m = new Maison("Maison1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g = new Generateur("Gen1", 100);

        r.ajouterMaison(m);
        r.ajouterGenerateur(g);
        r.ajouterConnexion(new Connexion(m, g));

        assertTrue(r.connexionExistante("Maison1 Gen1"));
        assertTrue(r.connexionExistante("Gen1 Maison1"));
        assertFalse(r.connexionExistante("Maison1 GenX"));
    }

    

    @Test
    public void testRecupererMaison() {
        Reseau r = new Reseau();
        r.ajouterMaison(new Maison("M1", TypeMaison.BASSE_CONSOMMATION));

        Maison m = r.recupererMaison("m1");
        assertNotNull(m);
        assertEquals("M1", m.getNom());
    }

    @Test
    public void testRecupererGenerateur() {
        Reseau r = new Reseau();
        r.ajouterGenerateur(new Generateur("G1", 100));

        Generateur g = r.recupererGenerateur("g1");
        assertNotNull(g);
        assertEquals("G1", g.getNom());
    }

    

    @Test
    public void testChargeGenerateur() {
        Reseau r = new Reseau();

        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Maison m2 = new Maison("M2", TypeMaison.FORTE_CONSOMMATION);
        Generateur g = new Generateur("G1", 100);

        r.ajouterMaison(m1);
        r.ajouterMaison(m2);
        r.ajouterGenerateur(g);
        r.ajouterConnexion(new Connexion(m1, g));
        r.ajouterConnexion(new Connexion(m2, g));

        double attendu = m1.getConsommation() + m2.getConsommation();
        assertEquals(attendu, r.chargeGenerateur(g), 0.001);
    }

    @Test
    public void testTauxUtilisation() {
        Reseau r = new Reseau();

        Maison m = new Maison("M1", TypeMaison.FORTE_CONSOMMATION);
        Generateur g = new Generateur("G1", 200);

        r.ajouterMaison(m);
        r.ajouterGenerateur(g);
        r.ajouterConnexion(new Connexion(m, g));

        double taux = r.tauxUtilisation(g);

        assertTrue(taux >= 0);
        assertTrue(taux <= 1);
    }

    @Test
    public void testMoyenneTauxUtilisation() {
        Reseau r = new Reseau();

        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 100);

        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);

        r.ajouterGenerateur(g1);
        r.ajouterGenerateur(g2);
        r.ajouterMaison(m1);
        r.ajouterConnexion(new Connexion(m1, g1));

        double moyenne = r.moyenneTauxUtilisation();
        assertTrue(moyenne >= 0);
    }

    @Test
    public void testDispersion() {
        Reseau r = new Reseau();

        Generateur g1 = new Generateur("G1", 50);
        Generateur g2 = new Generateur("G2", 200);

        Maison m = new Maison("M1", TypeMaison.FORTE_CONSOMMATION);

        r.ajouterGenerateur(g1);
        r.ajouterGenerateur(g2);
        r.ajouterMaison(m);
        r.ajouterConnexion(new Connexion(m, g1));

        assertTrue(r.dispersion() >= 0);
    }

    @Test
    public void testSurcharge() {
        Reseau r = new Reseau();

        Generateur g = new Generateur("G1", 1); // volontairement petit
        Maison m = new Maison("M1", TypeMaison.FORTE_CONSOMMATION);

        r.ajouterGenerateur(g);
        r.ajouterMaison(m);
        r.ajouterConnexion(new Connexion(m, g));

        assertTrue(r.surcharge() > 0);
    }

    @Test
    public void testCoutTotal() {
        Reseau r = new Reseau();
        r.setLambda(10);

        Generateur g = new Generateur("G1", 5);
        Maison m = new Maison("M1", TypeMaison.FORTE_CONSOMMATION);

        r.ajouterGenerateur(g);
        r.ajouterMaison(m);
        r.ajouterConnexion(new Connexion(m, g));

        double cout = r.coutTotal();
        assertTrue(cout >= 0);
    }

    // ====== Connexions avancées ======

    @Test
    public void testModifierConnexion() {
        Reseau r = new Reseau();

        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 100);

        r.ajouterMaison(m);
        r.ajouterGenerateur(g1);
        r.ajouterGenerateur(g2);
        r.ajouterConnexion(new Connexion(m, g1));

        boolean ok = r.modifierConnexion(m, g2);
        assertTrue(ok);

        assertTrue(r.connexionExistante("M1 G2"));
        assertFalse(r.connexionExistante("M1 G1"));
    }

    @Test
    public void testToutesMaisonsConnectees() {
        Reseau r = new Reseau();

        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Maison m2 = new Maison("M2", TypeMaison.BASSE_CONSOMMATION);

        Generateur g = new Generateur("G1", 100);

        r.ajouterMaison(m1);
        r.ajouterMaison(m2);
        r.ajouterGenerateur(g);

        List<Connexion> conns = new ArrayList<>();
        conns.add(new Connexion(m1, g));

        assertFalse(r.toutesMaisonsConnectees(conns));
    }

    @Test
    public void testOffreSuperieureOuEgaleADemande() {
        Reseau r = new Reseau();

        Generateur g = new Generateur("G1", 10000);
        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);

        r.ajouterGenerateur(g);
        r.ajouterMaison(m);

        assertTrue(r.offreSuperieureOuEgaleADemande());
    }
}
