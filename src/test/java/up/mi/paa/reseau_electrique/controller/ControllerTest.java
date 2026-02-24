package up.mi.paa.reseau_electrique.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import up.mi.paa.reseau_electrique.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private Reseau reseau;
    private Controller controller;

    @BeforeEach
    public void setUp() {
        reseau = new Reseau();
        controller = new Controller(reseau);
    }

    // ===== typeMaisonValide =====

    @Test
    public void testTypeMaisonValide() {
        assertTrue(Controller.typeMaisonValide("BASSE"));
        assertTrue(Controller.typeMaisonValide("MOYENNE"));
        assertTrue(Controller.typeMaisonValide("NORMAL"));
        assertTrue(Controller.typeMaisonValide("FORTE"));
        assertFalse(Controller.typeMaisonValide("INVALID"));
    }

    // ===== formatAjoutMaison =====

    @Test
    public void testFormatAjoutMaison() {
        assertTrue(Controller.formatAjoutMaison("Maison1 BASSE"));
        assertFalse(Controller.formatAjoutMaison("Maison1"));
        assertFalse(Controller.formatAjoutMaison("Maison1 123"));
    }

    // ===== formatAjoutGenerateur =====

    @Test
    public void testFormatAjoutGenerateur() {
        assertTrue(Controller.formatAjoutGenerateur("G1 50"));
        assertFalse(Controller.formatAjoutGenerateur("G1 CINQUANTE"));
        assertFalse(Controller.formatAjoutGenerateur("G1"));
    }

    // ===== formatAjoutConnexion =====

    @Test
    public void testFormatAjoutConnexion() {
        assertTrue(Controller.formatAjoutConnexion("G1 M1"));
        assertFalse(Controller.formatAjoutConnexion("G1"));
        assertFalse(Controller.formatAjoutConnexion(null));
    }

    // ===== ajouterMaison =====

    @Test
    public void testAjouterMaison() {
        assertTrue(controller.ajouterMaison("Maison1 BASSE"));
        assertTrue(reseau.maisonExistante("Maison1"));
    }

    // ===== ajouterGenerateur =====

    @Test
    public void testAjouterGenerateur() {
        assertTrue(controller.ajouterGenerateur("G1 100"));
        assertTrue(reseau.generateurExistant("G1"));
    }

    // ===== ajouterConnexion =====

    @Test
    public void testAjouterConnexion() {
        controller.ajouterMaison("M1 BASSE");
        controller.ajouterGenerateur("G1 50");

        List<Connexion> conns = new ArrayList<>();
        assertTrue(controller.ajouterConnexion("G1 M1", conns));
        assertEquals(1, conns.size());
    }

    // ===== verifierConnexions =====

    @Test
    public void testVerifierConnexions() {
        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g1 = new Generateur("G1", 50);
        Generateur g2 = new Generateur("G2", 50);

        List<Connexion> conns = new ArrayList<>();
        conns.add(new Connexion(m, g1));
        conns.add(new Connexion(m, g2)); // doublon

        List<Maison> doublons = controller.verifierConnexions(conns);
        assertEquals(1, doublons.size());
        assertEquals("M1", doublons.get(0).getNom());
    }

    // ===== supprimerConnexion =====

    @Test
    public void testSupprimerConnexion() {
        controller.ajouterMaison("M1 BASSE");
        controller.ajouterGenerateur("G1 50");

        Maison m = reseau.recupererMaison("M1");
        Generateur g = reseau.recupererGenerateur("G1");

        List<Connexion> conns = new ArrayList<>();
        conns.add(new Connexion(m, g));

        assertTrue(controller.supprimerConnexion("G1 M1", conns));
        assertEquals(0, conns.size());
    }
}
