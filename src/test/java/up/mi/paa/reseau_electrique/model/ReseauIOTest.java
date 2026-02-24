package up.mi.paa.reseau_electrique.model;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


import static org.junit.jupiter.api.Assertions.*;

public class ReseauIOTest {

    @Test
    public void testSauvegarderEtChargerReseau() throws IOException {
        
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

        
        File tempFile = File.createTempFile("reseau_test", ".txt");
        tempFile.deleteOnExit();

      
        ReseauIO.sauvegarder(r, tempFile.getAbsolutePath());


      
        Reseau loaded = ReseauIO.charger(tempFile.getAbsolutePath());

        
        assertEquals(r.getMaisons().size(), loaded.getMaisons().size());
        assertEquals(r.getGenerateurs().size(), loaded.getGenerateurs().size());
        assertEquals(r.getConnexions().size(), loaded.getConnexions().size());

        
        assertTrue(loaded.getMaisons().stream().anyMatch(m -> m.getNom().equals("M1")));
        assertTrue(loaded.getGenerateurs().stream().anyMatch(g -> g.getNom().equals("G2")));
    }

    @Test
    public void testChargerFichierInvalide() throws IOException {
        
        File tempFile = File.createTempFile("reseau_invalide", ".txt");
        tempFile.deleteOnExit();

        java.nio.file.Files.writeString(tempFile.toPath(), "maison(M1,BASSE)\ninvalid_line\n");

        assertThrows(IllegalArgumentException.class,
                () -> ReseauIO.charger(tempFile.getAbsolutePath()));
    }

    @Test
    public void testChargerFichierVide() throws IOException {
        File tempFile = File.createTempFile("reseau_vide", ".txt");
        tempFile.deleteOnExit();

        assertThrows(IllegalArgumentException.class,
                () -> ReseauIO.charger(tempFile.getAbsolutePath()));
    }
}
