package up.mi.paa.reseau_electrique.view;

/**
 * La classe View gère l'affichage et les interactions avec l'utilisateur
 * via la console en permettant la création et la gestion du réseau électrique.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import up.mi.paa.reseau_electrique.controller.Controller;
import up.mi.paa.reseau_electrique.model.Connexion;
import up.mi.paa.reseau_electrique.model.Generateur;
import up.mi.paa.reseau_electrique.model.Maison;
import up.mi.paa.reseau_electrique.model.Optimiseur;
import up.mi.paa.reseau_electrique.model.Reseau;
import up.mi.paa.reseau_electrique.model.ReseauIO;

public class View {

    // ==================== PARTIE 1 : MODE MANUEL ====================

    public static void lancerProg() {
        Scanner sc = new Scanner(System.in);

        Reseau reseau = new Reseau();
        Controller controller = new Controller(reseau);

        // liste temporaire de connexions tant qu'on est dans le menu 1
        List<Connexion> connexions = new ArrayList<>();

        int choix;

        // ============= MENU 1 =============
        do {
            System.out.println("\n==================================================");
            System.out.println("   MENU RÉSEAU ÉLECTRIQUE — PHASE DE CONSTRUCTION ");
            System.out.println("==================================================");
            System.out.println("1 - Ajouter une maison (format: nom type)");
            System.out.println("2 - Ajouter un générateur (format: nom capacité)");
            System.out.println("3 - Ajouter une connexion (format: nomGenerateur nomMaison)");
            System.out.println("4 - Supprimer une connexion");
            System.out.println("5 - Valider le réseau / passer à la phase 2");
            System.out.print("Votre choix : ");

            String saisies = sc.nextLine();
            try {
                choix = Integer.parseInt(saisies.trim());
            } catch (NumberFormatException e) {
                choix = -1;
                System.out.println("Saisie invalide, veuillez entrer un nombre compris entre 1 et 5.");
            }

            switch (choix) {

                // ----------- ajouter maison -----------
                case 1:
                    String nouvM;
                    do {
                        System.out.println(" Entrez une maison (ex: maison1 BASSE/NORMAL/FORTE) :");
                        nouvM = sc.nextLine();

                        if (controller.ajouterMaison(nouvM)) {
                            System.out.println("Maison ajoutée !");
                            break;
                        } else {
                            System.out.println(" Mauvais format ou Type invalide (BASSE / NORMAL / FORTE). Réessayez !");
                        }

                    } while (true);
                    break;

                // ----------- ajouter générateur -----------
                case 2:
                    String nouvG;
                    do {
                        System.out.println(" Entrez un générateur (ex: gen1 100) :");
                        nouvG = sc.nextLine();

                        if (controller.ajouterGenerateur(nouvG)) {
                            System.out.println(" Générateur ajouté !");
                            break;
                        } else {
                            System.out.println(" Capacité invalide (entier strictement positif requis).");
                        }

                    } while (true);
                    break;

                // ----------- ajouter connexion -----------
                case 3:
                    int x3 = 1;
                    do {
                        System.out.println(" Entrez une connexion (ex: gen1 maison1) :");
                        String nouvC = sc.nextLine();

                        if (controller.ajouterConnexion(nouvC, connexions)) {
                            System.out.println("Connexion ajoutée !");
                        } else {
                            System.out.println("Format invalide ou noms inexistants. Réessayez !");
                        }

                        System.out.println("Taper 1 pour ajouter d'autres connexions ou 0 pour revenir au menu :");
                        String saisie = sc.nextLine();
                        try {
                            x3 = Integer.parseInt(saisie.trim());
                        } catch (NumberFormatException e) {
                            x3 = 0; // si mauvaise saisie, on sort
                            System.out.println("Saisie invalide, retour au menu principal.");
                        }

                    } while (x3 == 1);
                    break;

                // ----------- supprimer une connexion -----------
                case 4:
                    System.out.println("Entrez la connexion à supprimer (ex: gen1 maison1) :");
                    String sup = sc.nextLine();
                    boolean ok = controller.supprimerConnexion(sup, connexions);
                    if (ok) {
                        System.out.println("Connexion supprimée.");
                    } else {
                        System.out.println("Connexion inexistante ou noms inconnus.");
                    }
                    break;

                // ----------- valider le réseau -----------
                case 5:

                    List<Maison> doublons = controller.verifierConnexions(connexions);
                    if (!reseau.toutesMaisonsConnectees(connexions)) {
                        System.out.println("Impossible de valider : certaines maisons ne sont pas connectées !");
                        break;
                    }
                    if(reseau.getGenerateurs().isEmpty()) {
                        System.out.println("Impossible de valider : il n'y a aucun generateur");
                        break;
                    }
                    if(reseau.getMaisons().isEmpty()) {
                        System.out.println("Impossible de valider : il n'y a aucune maison");
                        break;
                    }
                    
                    	

                    if (!reseau.offreSuperieureOuEgaleADemande()) {
                        System.out.println("Impossible de valider : la demande totale dépasse la capacité totale des générateurs !");
                        break;
                    }
                    if (doublons.isEmpty()) {
                        System.out.println("Aucune maison n’a plusieurs générateurs, réseau créé.");
                        controller.ajouterConnexionsRx(connexions);
                        choix = 0;
                        break;
                    }

                    System.out.println("Plusieurs maisons ont plusieurs générateurs :");
                    for (Maison m : doublons) {
                        System.out.println("- " + m.getNom());
                    }

                    System.out.println("\nChoisissez le mode de résolution :");
                    System.out.println("1) Suppression automatique (choix du générateur à garder)");
                    System.out.println("2) Suppression manuelle des connexions");
                    System.out.print("Votre choix : ");
                    String mode = sc.nextLine().trim();
                    if (mode.equals("1")) {
                        for (Maison maison : doublons) {
                            System.out.println("\nMaison " + maison.getNom() + " possède plusieurs générateurs.");

                            List<Connexion> connexionsMaison = new ArrayList<>();
                            for (Connexion c : connexions) {
                                if (c.getMaison().equals(maison)) {
                                    System.out.println("- " + c.getGenerateur().getNom());
                                    connexionsMaison.add(c);
                                }
                            }
                            //choisir la connexion a garder
                            Generateur garder = null;
                            while (garder == null) {
                                System.out.print("Entrez le nom du générateur à garder : ");
                                String choixG = sc.nextLine().trim();

                                for (Connexion c : connexionsMaison) {
                                    if (c.getGenerateur().getNom().equalsIgnoreCase(choixG)) {
                                        garder = c.getGenerateur();
                                        break;
                                    }
                                }

                                if (garder == null)
                                    System.out.println("Générateur invalide.");
                            }

                            controller.garderUnSeulGenerateur(maison, garder, connexions);
                        }
                    } else {
                        // mode 2 : on revient au menu pour supprimer à la main
                        break;
                    }
            }

        } while (choix != 0);

        // ============= MENU 2 =============
        // on ne lance le menu 2 que si le réseau est vraiment constitué
        if (!reseau.getMaisons().isEmpty()
                && !reseau.getGenerateurs().isEmpty()
                && !reseau.getConnexions().isEmpty()) {

            int choix2;
            do {
                System.out.println("\n==================================================");
                System.out.println("              MENU RÉSEAU — PHASE 2               ");
                System.out.println("==================================================");
                System.out.println("1 - Calculer le coût du réseau");
                System.out.println("2 - Modifier une connexion");
                System.out.println("3 - Afficher le réseau");
                System.out.println("4 - Quitter");
                System.out.print("Votre choix : ");

                String saisie2 = sc.nextLine().trim();
                try {
                    choix2 = Integer.parseInt(saisie2);
                } catch (NumberFormatException e) {
                    choix2 = -1;
                }

                switch (choix2) {
                    case 1:
                        afficherCout(reseau);
                        break;

                    case 2:
                        System.out.println("Entrez l'ancienne connexion (ex: M1 G1) :");
                        String ancienne = sc.nextLine();
                        System.out.println("Entrez la nouvelle connexion (ex: M1 G2) :");
                        String nouvelle = sc.nextLine();
                        boolean modif = controller.modifierConnexion(ancienne, nouvelle);
                        if (modif) {
                            System.out.println("Connexion modifiée.");
                        } else {
                            System.out.println("Erreur : connexion ou noms introuvables.");
                        }
                        break;

                    case 3:
                        reseau.affihcerRx();
                        break;

                    case 4:
                        System.out.println("Fin de la phase 2.");
                        break;

                    default:
                        System.out.println("Choix invalide.");
                }

            } while (choix2 != 4);
        }

        // affichage final
        reseau.affihcerRx();
        sc.close();
    }

    // ==================== PARTIE 2 : MODE FICHIER ====================

    public static void lancerProgFichier(Reseau reseau) {
        Scanner sc = new Scanner(System.in);
        boolean fini = false;

        while (!fini) {
            System.out.println("\n==================================================");
            System.out.println("               MENU PARTIE 2 (FICHIER)            ");
            System.out.println("==================================================");
            System.out.println("1) Résolution automatique");
            System.out.println("2) Sauvegarder la solution actuelle");
            System.out.println("3) Fin");
            System.out.print("Votre choix : ");

            String saisie = sc.nextLine().trim();
            int choix;
            try {
                choix = Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Erreur : veuillez saisir 1, 2 ou 3.");
                continue;
            }

            switch (choix) {
                case 1:
                    lancerResolutionAuto(reseau, sc);
                    break;
                case 2:
                    sauvegarderSolution(reseau, sc);
                    break;
                case 3:
                    fini = true;
                    System.out.println("Fin du programme (partie 2).");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez saisir 1, 2 ou 3.");
            }
        }
        // on ne ferme pas sc ici pour ne pas impacter d'autres entrées potentielles
    }

    // ==================== OUTILS D'AFFICHAGE ====================

    private static void lancerResolutionAuto(Reseau reseau, Scanner sc) {
        System.out.println("\n=========== RÉSOLUTIONS AUTOMATIQUE ===========");
        double coutAvant = reseau.coutTotal();
        System.out.printf("Coût actuel : %.3f%n", coutAvant);
        

        
        int iterations;


        // Affichage avant optimisation
        System.out.println("\n>>> Réseau AVANT optimisation :");
        reseau.affihcerRx();

        iterations = Optimiseur.optimiser(reseau);

        // Affichage après optimisation
        System.out.println("\n>>> Réseau APRÈS optimisation :");
        reseau.affihcerRx();
        
        
        System.out.println("\n>>> Cout du reseau apres optimisation :");
        afficherCout(reseau);
        
        double coutApres = reseau.coutTotal();
        System.out.printf("Coût avant : %.3f   |   Coût après : %.3f%n", coutAvant, coutApres);
        System.out.println("Le nombre d'iteration est : " + iterations);
        if (coutApres < coutAvant) {
            System.out.println("Optimisation réussie !");
            
            
        } else if (coutApres == coutAvant) {
            System.out.println("Aucun changement (minimum local).");
        } else {
            System.out.println("Le coût a augmenté (peu probable avec beaucoup d'itérations).");
        }
    }

    private static void sauvegarderSolution(Reseau reseau, Scanner sc) {
        System.out.print("\nNom du fichier de sauvegarde (différent du fichier d'entrée) : ");
        String filename = sc.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Erreur : nom de fichier vide.");
            return;
        }

        ReseauIO.sauvegarder(reseau, filename);
        System.out.println("Solution sauvegardée dans " + filename);
    }

    private static void afficherCout(Reseau reseau) {
        System.out.println("\n=========== ANALYSE DU COÛT DU RÉSEAU ===========");

        double dispersion = reseau.dispersion();
        double surcharge = reseau.surcharge();
        double cout = reseau.coutTotal();
        double moyenne = reseau.moyenneTauxUtilisation();

        System.out.printf("Moyenne des taux d’utilisation : %.3f%n", moyenne);
        System.out.println("Écarts individuels :");

        for (Generateur g : reseau.getGenerateurs()) {
            double u = reseau.tauxUtilisation(g);
            double ecart = Math.abs(u - moyenne);
            System.out.printf("- %s : %.3f%n", g.getNom(), ecart);
        }

        System.out.printf("Dispersion totale : %.3f%n", dispersion);
        System.out.printf("Surcharge totale : %.3f%n", surcharge);
        System.out.printf("Coût total (λ=%d) : %.3f%n", reseau.getLambda(), cout);

        System.out.println("=================================================\n");
    }
}
