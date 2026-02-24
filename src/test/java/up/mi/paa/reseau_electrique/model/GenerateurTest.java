package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenerateurTest {

    @Test
    public void testConstructeurEtGetters() {
        Generateur g = new Generateur("Gen1", 100);

        assertEquals("Gen1", g.getNom());
        assertEquals(100, g.getCapacite());
    }

    @Test
    public void testSetters() {
        Generateur g = new Generateur("Ancien", 50);

        g.setNom("Nouveau");
        g.setCapacite(200);

        assertEquals("Nouveau", g.getNom());
        assertEquals(200, g.getCapacite());
    }

    @Test
    public void testEqualsMemeNomCasseDifferente() {
        Generateur g1 = new Generateur("GenA", 100);
        Generateur g2 = new Generateur("gena", 999);

        assertTrue(g1.equals(g2));
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    public void testEqualsNomDifferent() {
        Generateur g1 = new Generateur("GenA", 100);
        Generateur g2 = new Generateur("GenB", 100);

        assertFalse(g1.equals(g2));
        assertNotEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    public void testEqualsAvecNullEtAutreClasse() {
        Generateur g = new Generateur("Gen", 10);

        assertFalse(g.equals(null));
        assertFalse(g.equals("pas un generateur"));
    }

    @Test
    public void testToString() {
        Generateur g = new Generateur("MonGen", 50);

        assertEquals("MonGen", g.toString());
    }
}
