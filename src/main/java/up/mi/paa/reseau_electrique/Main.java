package up.mi.paa.reseau_electrique;
/**
 * Classe principale du projet Réseau électrique.
 * 
 * Cette classe permet de lancer l'application en mode :
 * 1. Interface graphique (JavaFX) ou
 * 2. Interface textuelle (console)
 * 
 * Le choix de l'interface est demandé à l'utilisateur via la console.
 * 
 * Paramètres possibles via args (CLI) :
 *  args[0] : chemin vers un fichier réseau à charger
 *  args[1] : valeur de lambda (optionnel, défaut = 10)
 */

import java.util.Scanner;

import javafx.application.Application;
import up.mi.paa.reseau_electrique.model.Reseau;
import up.mi.paa.reseau_electrique.model.ReseauIO;
import up.mi.paa.reseau_electrique.view.ReseauApp;
import up.mi.paa.reseau_electrique.view.View;

public class Main {

    public static void main(String[] args) {
    	
    	int choix;
    	Scanner sc = new Scanner(System.in);
        do {
            System.out.println("\n==================================================");
            System.out.println("              CHOIX DE L'INTERFACE               ");
            System.out.println("==================================================");
            System.out.println("1 - Interface graphique");
            System.out.println("2 - Interface textuelle");
 

            String saisie = sc.nextLine().trim();
            try {
                choix = Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                choix = -1;
            }

            

        } while (choix != 2 && choix != 1);
        
        switch (choix) {
        case 1:
        	Application.launch(ReseauApp.class, args);
        	break;
        	
        case 2:
            // CAS 1 : aucune option → PARTIE 1 (mode manuel, construction à la main)
            if (args.length == 0) {
                View.lancerProg();
            }
            // CAS 2 : fichier + lambda → PARTIE 2 (mode fichier)
            else {
                String filename = args[0];
                int lambda = 10; // valeur par défaut

                if (args.length >= 2) {
                    try {
                        lambda = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Lambda invalide, utilisation de 10 par défaut.");
                    }
                }

                try {
                    Reseau r = ReseauIO.charger(filename);
                    r.setLambda(lambda);
                    View.lancerProgFichier(r);
                } catch (Exception e) {
                    System.out.println("Erreur : " + e.getMessage());
                }
            }
        }

sc.close();
    }
}
