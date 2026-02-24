==================================================
        PROJET PAA – RÉSEAU ÉLECTRIQUE
==================================================

Auteurs :
- Djamila Khoulalene
- Yanis Hammaoui

Année universitaire : 2025–2026
UE : Programmation Avancée et Applications (PAA)

--------------------------------------------------
1. PRÉSENTATION DU PROJET
--------------------------------------------------

Ce projet a pour objectif de modéliser, analyser et optimiser un réseau
électrique composé de générateurs, de maisons et de connexions entre eux.

Chaque générateur possède une capacité maximale (en kW) et chaque maison
une consommation dépendant de son type. Chaque maison doit être reliée à
un unique générateur.

Le programme permet :
- la construction manuelle du réseau,
- la vérification de sa validité,
- le calcul d’un coût basé sur la dispersion et la surcharge,
- l’optimisation automatique du réseau,
- l’import/export du réseau via un fichier texte,
- une interface console et une interface graphique JavaFX.

Le projet respecte l’architecture MVC (Modèle / Vue / Contrôleur).

--------------------------------------------------
2. ARCHITECTURE DU PROJET (MVC)
--------------------------------------------------

Le projet est structuré selon l’architecture MVC :

- Modèle (model) :
  Contient la logique métier et les règles fondamentales du réseau.
  Toute incohérence (capacité négative, connexions invalides, etc.)
  est empêchée à ce niveau.

- Contrôleur (controller) :
  Gère la validation des entrées utilisateur et orchestre les actions
  entre la vue et le modèle.

- Vue (view) :
  Fournit deux interfaces accessibles depuis un menu principal :
  - une interface console,
  - une interface graphique JavaFX.

--------------------------------------------------
3. ARBORESCENCE DU PROJET
--------------------------------------------------

.
├── bin
│   ├── target
│   │   ├── classes
│   │   │   ├── META-INF
│   │   │   │   ├── MANIFEST.MF
│   │   │   │   └── maven
│   │   │   │       └── up.mi.paa
│   │   │   │           └── reseau-electrique
│   │   │   │               ├── pom.properties
│   │   │   │               └── pom.xml
│   │   │   └── up
│   │   │       └── mi
│   │   │           └── paa
│   │   │               └── reseau_electrique
│   │   │                   ├── controller
│   │   │                   ├── model
│   │   │                   └── view
│   │   └── test-classes
│   │       └── up
│   │           └── mi
│   │               └── paa
│   │                   └── reseau_electrique
│   └── up
│       └── mi
│           └── paa
│               └── reseau_electrique
│                   ├── controller
│                   │   └── Controller.class
│                   ├── gui
│                   │   └── Launcher.class
│                   ├── GuiMain.class
│                   ├── Main.class
│                   ├── MainTestOptimiseur.class
│                   ├── model
│                   │   ├── Connexion.class
│                   │   ├── Generateur.class
│                   │   ├── Maison.class
│                   │   ├── Optimiseur.class
│                   │   ├── Reseau.class
│                   │   ├── ReseauIO.class
│                   │   └── TypeMaison.class
│                   └── view
│                       ├── ReseauApp.class
│                       ├── ReseauApp$1.class
│                       ├── ReseauApp$2.class
│                       ├── ReseauApp$3.class
│                       ├── ReseauApp$4.class
│                       ├── ReseauApp$5.class
│                       ├── ReseauApp$6.class
│                       ├── ReseauFrame.class
│                       ├── ReseauFrame$1.class
│                       └── View.class
├── instance3.txt
├── pom.xml
├── README.txt
├── README.txt.save
├── reseau-electrique.jar
├── src
│   ├── main
│   │   └── java
│   │       └── up
│   │           └── mi
│   │               └── paa
│   │                   └── reseau_electrique
│   │                       ├── controller
│   │                       │   └── Controller.java
│   │                       ├── Main.java
│   │                       ├── model
│   │                       │   ├── Connexion.java
│   │                       │   ├── Generateur.java
│   │                       │   ├── Maison.java
│   │                       │   ├── Optimiseur.java
│   │                       │   ├── Reseau.java
│   │                       │   ├── ReseauIO.java
│   │                       │   └── TypeMaison.java
│   │                       └── view
│   │                           ├── ReseauApp.java
│   │                           └── View.java
│   └── test
│       └── java
│           └── up
│               └── mi
│                   └── paa
│                       └── reseau_electrique
│                           ├── controller
│                           │   └── ControllerTest.java
│                           └── model
│                               ├── ConnexionTest.java
│                               ├── GenerateurTest.java
│                               ├── MaisonTest.java
│                               ├── OptimiseurTest.java
│                               ├── ReseauIOTest.java
│                               ├── ReseauTest.java
│                               └── TypeMaisonTest.java
└── target
    ├── classes
    │   └── up
    │       └── mi
    │           └── paa
    │               └── reseau_electrique
    │                   ├── controller
    │                   │   └── Controller.class
    │                   ├── Main.class
    │                   ├── model
    │                   │   ├── Connexion.class
    │                   │   ├── Generateur.class
    │                   │   ├── Maison.class
    │                   │   ├── Optimiseur.class
    │                   │   ├── Reseau.class
    │                   │   ├── ReseauIO.class
    │                   │   └── TypeMaison.class
    │                   └── view
    │                       ├── ReseauApp.class
    │                       ├── ReseauApp$1.class
    │                       ├── ReseauApp$2.class
    │                       ├── ReseauApp$3.class
    │                       ├── ReseauApp$4.class
    │                       ├── ReseauApp$5.class
    │                       ├── ReseauApp$6.class
    │                       └── View.class
    ├── generated-sources
    │   └── annotations
    ├── generated-test-sources
    │   └── test-annotations
    ├── maven-status
    │   └── maven-compiler-plugin
    │       ├── compile
    │       │   └── default-compile
    │       │       ├── createdFiles.lst
    │       │       └── inputFiles.lst
    │       └── testCompile
    │           └── default-testCompile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    ├── surefire-reports
    │   ├── TEST-up.mi.paa.reseau_electrique.controller.ControllerTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.ConnexionTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.GenerateurTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.MaisonTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.OptimiseurTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.ReseauIOTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.ReseauTest.xml
    │   ├── TEST-up.mi.paa.reseau_electrique.model.TypeMaisonTest.xml
    │   ├── up.mi.paa.reseau_electrique.controller.ControllerTest.txt
    │   ├── up.mi.paa.reseau_electrique.model.ConnexionTest.txt
    │   ├── up.mi.paa.reseau_electrique.model.GenerateurTest.txt
    │   ├── up.mi.paa.reseau_electrique.model.MaisonTest.txt
    │   ├── up.mi.paa.reseau_electrique.model.OptimiseurTest.txt
    │   ├── up.mi.paa.reseau_electrique.model.ReseauIOTest.txt
    │   ├── up.mi.paa.reseau_electrique.model.ReseauTest.txt
    │   └── up.mi.paa.reseau_electrique.model.TypeMaisonTest.txt
    └── test-classes
        └── up
            └── mi
                └── paa
                    └── reseau_electrique
                        ├── controller
                        │   └── ControllerTest.class
                        └── model
                            ├── ConnexionTest.class
                            ├── GenerateurTest.class
                            ├── MaisonTest.class
                            ├── OptimiseurTest.class
                            ├── ReseauIOTest.class
                            ├── ReseauTest.class
                            └── TypeMaisonTest.class

73 directories, 94 files

--------------------------------------------------
4. TYPES DE MAISONS
--------------------------------------------------

Les types de maisons sont conformes à l’énoncé :

- BASSE  : 10 kW
- NORMAL : 20 kW
- FORTE  : 40 kW

Le programme accepte éventuellement le terme "MOYENNE" en entrée
(utilisateur ou fichier), mais affiche et sauvegarde toujours "NORMAL"
afin de rester conforme à l’énoncé.

--------------------------------------------------
5. CONTRAINTES DU RÉSEAU
--------------------------------------------------

Les contraintes suivantes sont strictement respectées :

- La capacité d’un générateur doit être strictement positive (> 0).
- Chaque maison est connectée à un unique générateur.
- Toutes les maisons doivent être connectées.
- La capacité totale des générateurs doit être supérieure ou égale
  à la demande totale des maisons.
- Le coût du réseau est défini par :

  Cout = Dispersion + λ × Surcharge

--------------------------------------------------
6. ALGORITHME D’OPTIMISATION
--------------------------------------------------

Le projet implémente un algorithme d’optimisation inspiré du recuit simulé.

Principe général :
- On part d’un réseau valide initial.
- À chaque itération, une maison est choisie aléatoirement.
- Sa connexion est temporairement modifiée vers un autre générateur.
- Le nouveau coût du réseau est calculé.

Règle d’acceptation :
- Si le coût diminue, la modification est acceptée.
- Si le coût augmente, la modification peut être acceptée avec une
  probabilité dépendant de la température (fonction exponentielle).

Ce mécanisme permet :
- d’explorer l’espace des solutions,
- d’éviter de rester bloqué dans des minima locaux,
- de converger vers une solution de coût plus faible.

La température décroît progressivement au fil des itérations
(refroidissement), ce qui réduit la probabilité d’accepter des solutions
moins bonnes à la fin de l’algorithme.

L’algorithme garantit :
- la validité permanente du réseau,
- qu’aucune maison n’est supprimée,
- que chaque maison reste connectée à un unique générateur.

--------------------------------------------------
7. EXÉCUTION DU PROGRAMME
--------------------------------------------------

Le projet est fourni sous forme de JAR exécutable.

Prérequis :
- Java 17 ou supérieur installé
- JavaFX requis uniquement pour l’interface graphique

Lancement (menu principal) :
Depuis la racine du projet, exécuter :

java -jar reseau-electrique-1.0-SNAPSHOT.jar instance1.txt 30

Un menu principal unique s’affiche alors :

1 - Interface graphique (JavaFX)
2 - Interface textuelle

L’utilisateur choisit le mode souhaité.

--------------------------------------------------
8. EXÉCUTION AVEC INTERFACE GRAPHIQUE (JavaFX)
--------------------------------------------------

Si JavaFX n’est pas inclus dans l’environnement Java, il est nécessaire
de fournir explicitement le chemin vers le JavaFX SDK.

Commande générale :

java --module-path <CHEMIN_VERS_JAVAFX_SDK>/lib --add-modules javafx.controls,javafx.fxml -jar reseau-electrique-1.0-SNAPSHOT.jar instance1.txt 30

Exemple (macOS) :

java --module-path ~/javafx/javafx-sdk-17.0.17/lib --add-modules javafx.controls,javafx.fxml -jar reseau-electrique-1.0-SNAPSHOT.jar instance1.txt 30

--------------------------------------------------
9. UTILISATION – INTERFACE CONSOLE
--------------------------------------------------

L’interface console permet :
- la construction manuelle du réseau,
- la validation des contraintes,
- le calcul du coût,
- l’optimisation automatique,
- la sauvegarde de la solution.

Elle ne nécessite aucune dépendance graphique et fonctionne sur toutes
les plateformes (macOS, Linux, Windows).


--------------------------------------------------
10. FORMAT DES FICHIERS (PARTIE 2)
--------------------------------------------------

Exemple de fichier valide :

generateur(G1,100).
generateur(G2,80).
maison(M1,NORMAL).
maison(M2,BASSE).
connexion(G1,M1).
connexion(G2,M2).

Contraintes :
- ordre obligatoire : générateurs, maisons, connexions,
- point final obligatoire,
- références définies avant utilisation,
- capacités strictement positives.

--------------------------------------------------
11. TESTS
--------------------------------------------------

Le projet inclut une suite de tests unitaires JUnit couvrant :
- le modèle,
- le contrôleur,
- le parsing des fichiers,
- l’algorithme d’optimisation.

Les tests permettent de vérifier la robustesse et la cohérence du programme.

--------------------------------------------------
12. AUTEURS
--------------------------------------------------

Projet réalisé par :
- Djamila Khoulalene
- Yanis Hammaoui

Dans le cadre du cours de Programmation Avancée et Applications (PAA).

==================================================



