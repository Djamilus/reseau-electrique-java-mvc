package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TypeMaisonTest {

   

    @Test
    public void testGetConsommationKW() {
        assertEquals(10, TypeMaison.BASSE_CONSOMMATION.getConsommationKW());
        assertEquals(20, TypeMaison.MOYENNE_CONSOMMATION.getConsommationKW());
        assertEquals(40, TypeMaison.FORTE_CONSOMMATION.getConsommationKW());
    }

  

    @Test
    public void testStringToTypeMaisonBasse() {
        assertEquals(TypeMaison.BASSE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("BASSE"));
        assertEquals(TypeMaison.BASSE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("basse"));
    }

    @Test
    public void testStringToTypeMaisonMoyenne() {
        assertEquals(TypeMaison.MOYENNE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("MOYENNE"));
        assertEquals(TypeMaison.MOYENNE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("moyenne"));
        assertEquals(TypeMaison.MOYENNE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("NORMAL"));
        assertEquals(TypeMaison.MOYENNE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("normal"));
    }

    @Test
    public void testStringToTypeMaisonForte() {
        assertEquals(TypeMaison.FORTE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("FORTE"));
        assertEquals(TypeMaison.FORTE_CONSOMMATION,
                TypeMaison.stringToTypeMaison("forte"));
    }

 

    @Test
    public void testStringToTypeMaisonInvalide() {
        assertNull(TypeMaison.stringToTypeMaison("INCONNU"));
        assertNull(TypeMaison.stringToTypeMaison(""));
        assertNull(TypeMaison.stringToTypeMaison("123"));
    }

    @Test
    public void testStringToTypeMaisonNull() {
        assertNull(TypeMaison.stringToTypeMaison(null));
    }
}
