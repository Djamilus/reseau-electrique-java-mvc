package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MaisonTest {

    @Test
    public void testConstructorAndGetters() {
        TypeMaison type = TypeMaison.MOYENNE_CONSOMMATION; 
        Maison m = new Maison("Maison1", type);

        assertEquals("Maison1", m.getNom());
        assertEquals(type, m.getType());
    }

    @Test
    public void testSetters() {
        Maison m = new Maison("AncienNom", TypeMaison.FORTE_CONSOMMATION);

        m.setNom("NouveauNom");
        m.setType(TypeMaison.BASSE_CONSOMMATION);

        assertEquals("NouveauNom", m.getNom());
        assertEquals(TypeMaison.BASSE_CONSOMMATION, m.getType());
    }

    @Test
    public void testGetConsommation() {
        TypeMaison type = TypeMaison.BASSE_CONSOMMATION;
        Maison m = new Maison("Test", type);

        assertEquals(type.getConsommationKW(), m.getConsommation());
    }

    @Test
    public void testEqualsSameNameDifferentCase() {
        Maison m1 = new Maison("MaisonA", TypeMaison.FORTE_CONSOMMATION);
        Maison m2 = new Maison("maisona", TypeMaison.BASSE_CONSOMMATION);

        assertTrue(m1.equals(m2));
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    public void testEqualsDifferentName() {
        Maison m1 = new Maison("MaisonA", TypeMaison.FORTE_CONSOMMATION);
        Maison m2 = new Maison("MaisonB", TypeMaison.FORTE_CONSOMMATION);

        assertFalse(m1.equals(m2));
        assertNotEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    public void testEqualsWithNullAndDifferentClass() {
        Maison m = new Maison("Test", TypeMaison.BASSE_CONSOMMATION);

        assertFalse(m.equals(null));
        assertFalse(m.equals("Une chaîne"));
    }
}

