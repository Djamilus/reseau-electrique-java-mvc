package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnexionTest {

    @Test
    public void testConstructeurEtGetters() {
        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g = new Generateur("G1", 100);

        Connexion c = new Connexion(m, g);

        assertEquals(m, c.getMaison());
        assertEquals(g, c.getGenerateur());
    }

    @Test
    public void testSetters() {
        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Maison m2 = new Maison("M2", TypeMaison.FORTE_CONSOMMATION);

        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 200);

        Connexion c = new Connexion(m1, g1);

        c.setMaison(m2);
        c.setGenerateur(g2);

        assertEquals(m2, c.getMaison());
        assertEquals(g2, c.getGenerateur());
    }

    @Test
    public void testEqualsMemeObjet() {
        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g = new Generateur("G1", 100);

        Connexion c = new Connexion(m, g);

        assertTrue(c.equals(c));
    }

    @Test
    public void testEqualsValeursIdentiques() {
        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g1 = new Generateur("G1", 100);

        Maison m2 = new Maison("m1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g2 = new Generateur("g1", 50); // capacité différente pour tester equals insensible

        Connexion c1 = new Connexion(m1, g1);
        Connexion c2 = new Connexion(m2, g2);

        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void testEqualsValeursDifferentes() {
        Maison m1 = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g1 = new Generateur("G1", 100);

        Maison m2 = new Maison("M2", TypeMaison.BASSE_CONSOMMATION);
        Generateur g2 = new Generateur("G2", 100);

        Connexion c1 = new Connexion(m1, g1);
        Connexion c2 = new Connexion(m2, g2);

        assertFalse(c1.equals(c2));
        assertNotEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void testEqualsAvecNullEtAutreClasse() {
        Maison m = new Maison("M1", TypeMaison.BASSE_CONSOMMATION);
        Generateur g = new Generateur("G1", 100);

        Connexion c = new Connexion(m, g);

        assertFalse(c.equals(null));
        assertFalse(c.equals("pas une connexion"));
    }
}

