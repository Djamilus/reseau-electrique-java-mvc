package up.mi.paa.reseau_electrique.view;
/**
 * La classe ReseauApp est l'application JavaFX principale
 * pour visualiser, construire et analyser un réseau électrique.
 * Elle fournit plusieurs onglets pour :
 * Voir le réseau actuel (maisons, générateurs, connexions)
 * Ajouter ou modifier des maisons, générateurs et connexions
 * Calculer le coût et optimiser le réseau
 * Importer ou sauvegarder des fichiers de réseau
 * Cette classe interagit avec le Controller et le modèle Reseau}.
 */

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import up.mi.paa.reseau_electrique.controller.Controller;
import up.mi.paa.reseau_electrique.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReseauApp extends Application {



    // ====== Modèle ======
    private Reseau reseau;
    private Controller controller;

    // ====== Données observables pour la GUI ======
    private ObservableList<Maison> maisonsObservable;
    private ObservableList<Generateur> generateursObservable;
    private ObservableList<Connexion> connexionsObservable;

    // ====== Composants de tables / listes ======
    private TableView<Maison> tableMaisons;
    private TableView<Generateur> tableGenerateurs;
    private ListView<String> listConnParGenerateur;

    // ====== Labels pour l'analyse / coût ======
    private Label lblMoyenne;
    private Label lblDispersion;
    private Label lblSurcharge;
    private Label lblCout;
    private Label lblLambda;
    private Label lblValidite;
    private Label lblMsgOpt;

    // Résumé du réseau
    private Label lblResumeReseau;

    // Infos fichier
    private Label lblFichierActuel;
    @Override
    public void init() {
        List<String> args = getParameters().getRaw();

        reseau = new Reseau();

        if (args.size() >= 1) {
            String filename = args.get(0);
            int lambda = 10;

            if (args.size() >= 2) {
                try {
                    lambda = Integer.parseInt(args.get(1));
                } catch (NumberFormatException e) {
                    System.out.println("Lambda invalide, utilisation de 10 par défaut.");
                }
            }

            try {
                reseau = ReseauIO.charger(filename);
                reseau.setLambda(lambda);
                System.out.println("Fichier chargé : " + filename + ", λ=" + lambda);
            } catch (Exception e) {
                System.out.println("Erreur chargement fichier : " + e.getMessage());
                reseau = new Reseau(); // fallback
            }
        }
    }


    @Override
    public void start(Stage primaryStage) {

       
        controller = new Controller(reseau);

        // Listes observables
        maisonsObservable = FXCollections.observableArrayList(reseau.getMaisons());
        generateursObservable = FXCollections.observableArrayList(reseau.getGenerateurs());
        connexionsObservable = FXCollections.observableArrayList(reseau.getConnexions());

        // ==== Onglets ====
        TabPane tabPane = new TabPane();

        Tab tabReseau = new Tab("Réseau actuel");
        tabReseau.setClosable(false);
        tabReseau.setContent(buildReseauView());

        Tab tabEdition = new Tab("Construction / édition");
        tabEdition.setClosable(false);
        tabEdition.setContent(buildEditionView());

        Tab tabAnalyse = new Tab("Coût & optimisation");
        tabAnalyse.setClosable(false);
        tabAnalyse.setContent(buildAnalyseView());

        Tab tabFichier = new Tab("Fichier (import / sauvegarde)");
        tabFichier.setClosable(false);
        tabFichier.setContent(buildFichierView());

        tabPane.getTabs().addAll(tabReseau, tabEdition, tabAnalyse, tabFichier);

        Scene scene = new Scene(tabPane, 1100, 650);
        primaryStage.setTitle("Réseau électrique - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        // initialisation des labels
        refreshAllViews();
        updateCostLabels();
        lblFichierActuel.setText(
        	    getParameters().getRaw().isEmpty()
        	        ? "Aucun fichier chargé."
        	        : "Chargé depuis arguments."
        	);

    }

    // =========================================================
    //  ONGLET 1 : VUE DU RÉSEAU (tables + couleurs + connexions regroupées)
    // =========================================================
    private BorderPane buildReseauView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // ===== Résumé du réseau + légende couleurs =====
        lblResumeReseau = new Label("Réseau vide.");
        lblResumeReseau.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Label lblLegende = new Label(
                "Légende couleurs générateurs :  Vert < 70%  |  Orange 70–100%  |  Rouge > 100% (surcharge)"
        );
        lblLegende.setStyle("-fx-font-size: 11;");

        VBox topBox = new VBox(lblResumeReseau, lblLegende);
        topBox.setSpacing(5);
        root.setTop(topBox);

        // ===== TABLE GÉNÉRATEURS =====
        tableGenerateurs = new TableView<>();
        tableGenerateurs.setItems(generateursObservable);

        TableColumn<Generateur, String> colNomG = new TableColumn<>("Nom");
        colNomG.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom()));

        TableColumn<Generateur, Number> colCapacite = new TableColumn<>("Capacité (kW)");
        colCapacite.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getCapacite()));

        TableColumn<Generateur, Number> colCharge = new TableColumn<>("Charge (kW)");
        colCharge.setCellValueFactory(data ->
                new SimpleDoubleProperty(reseau.chargeGenerateur(data.getValue())));

        TableColumn<Generateur, Number> colUtil = new TableColumn<>("Utilisation (%)");
        colUtil.setCellValueFactory(data ->
                new SimpleDoubleProperty(reseau.tauxUtilisation(data.getValue()) * 100.0));

        tableGenerateurs.getColumns().addAll(colNomG, colCapacite, colCharge, colUtil);

        // RowFactory : couleurs selon utilisation
        tableGenerateurs.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Generateur g, boolean empty) {
                super.updateItem(g, empty);
                if (empty || g == null) {
                    setStyle("");
                } else {
                    double u = reseau.tauxUtilisation(g);
                    if (u < 0.7) {
                        setStyle("-fx-background-color: #e7f9e7;"); // vert clair
                    } else if (u < 1.0) {
                        setStyle("-fx-background-color: #fff7e6;"); // orange clair
                    } else {
                        setStyle("-fx-background-color: #ffe6e6;"); // rouge clair
                    }
                }
            }
        });

        VBox boxG = new VBox(new Label("Générateurs"), tableGenerateurs);
        boxG.setSpacing(5);

        // ===== TABLE MAISONS =====
        tableMaisons = new TableView<>();
        tableMaisons.setItems(maisonsObservable);

        TableColumn<Maison, String> colNomM = new TableColumn<>("Nom");
        colNomM.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom()));

        TableColumn<Maison, String> colTypeM = new TableColumn<>("Type");
        colTypeM.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getType() != null
                                ? TypeMaison.typeToString(data.getValue().getType())
                                : ""
                ));

        TableColumn<Maison, Number> colConso = new TableColumn<>("Conso (kW)");
        colConso.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getConsommation()));

        tableMaisons.getColumns().addAll(colNomM, colTypeM, colConso);

        VBox boxM = new VBox(new Label("Maisons"), tableMaisons);
        boxM.setSpacing(5);

        SplitPane topSplit = new SplitPane(boxG, boxM);
        topSplit.setDividerPositions(0.5);
        root.setCenter(topSplit);

        // ===== Connexions regroupées par générateur =====
        listConnParGenerateur = new ListView<>();
        VBox boxConn = new VBox(new Label("Connexions par générateur"), listConnParGenerateur);
        boxConn.setSpacing(5);
        boxConn.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(boxConn);

        return root;
    }

    // =========================================================
    //  ONGLET 2 : CONSTRUCTION / ÉDITION (manuel)
    // =========================================================
    private ScrollPane buildEditionView() {

        VBox root = new VBox();
        root.setSpacing(20);
        root.setPadding(new Insets(15));

        // ======== SECTION GÉNÉRATEUR ========
        Label titreGen = new Label("Ajouter / modifier un générateur");
        titreGen.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField tfNomGen = new TextField();
        tfNomGen.setPromptText("Nom du générateur (ex: G1)");

        TextField tfCapaciteGen = new TextField();
        tfCapaciteGen.setPromptText("Capacité (kW) (ex: 100)");

        Button btnAjoutGen = new Button("Ajouter / mettre à jour générateur");
        Label lblMsgGen = new Label();

        btnAjoutGen.setOnAction(e -> {
            String nom = tfNomGen.getText().trim();
            String capaciteStr = tfCapaciteGen.getText().trim();

            if (nom.isEmpty() || capaciteStr.isEmpty()) {
                lblMsgGen.setText("Veuillez remplir nom et capacité.");
                return;
            }

            String saisie = nom + " " + capaciteStr;
            boolean ok = controller.ajouterGenerateur(saisie);

            if (ok) {
                refreshAllViews();
                updateCostLabels();
                lblMsgGen.setText("Générateur ajouté / mis à jour !");
                tfNomGen.clear();
                tfCapaciteGen.clear();
            } else {
                lblMsgGen.setText("Format invalide (ex: G1 100).");
            }
        });

        VBox blocGen = new VBox(titreGen, tfNomGen, tfCapaciteGen, btnAjoutGen, lblMsgGen);
        blocGen.setSpacing(5);
        blocGen.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");

        // ======== SECTION MAISON ========
        Label titreMaison = new Label("Ajouter / modifier une maison");
        titreMaison.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TextField tfNomMaison = new TextField();
        tfNomMaison.setPromptText("Nom de la maison (ex: M1)");

        ComboBox<String> cbTypeMaison = new ComboBox<>();
        cbTypeMaison.getItems().addAll("BASSE", "NORMAL", "FORTE");
        cbTypeMaison.setPromptText("Type de consommation");
        cbTypeMaison.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Type de consommation" : item);
            }
        });

        Button btnAjoutMaison = new Button("Ajouter / mettre à jour maison");
        Label lblMsgMaison = new Label();

        btnAjoutMaison.setOnAction(e -> {
            String nom = tfNomMaison.getText().trim();
            String type = cbTypeMaison.getValue();

            if (nom.isEmpty() || type == null) {
                lblMsgMaison.setText("Veuillez remplir nom et type.");
                return;
            }

            String saisie = nom + " " + type;
            boolean ok = controller.ajouterMaison(saisie);

            if (ok) {
                refreshAllViews();
                updateCostLabels();
                lblMsgMaison.setText("Maison ajoutée / mise à jour !");
                tfNomMaison.clear();
                cbTypeMaison.setValue(null);
            } else {
                lblMsgMaison.setText("Format ou type invalide (BASSE/NORMAL/FORTE).");
            }
        });

        VBox blocMaison = new VBox(titreMaison, tfNomMaison, cbTypeMaison, btnAjoutMaison, lblMsgMaison);
        blocMaison.setSpacing(5);
        blocMaison.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");

        // ======== SECTION CONNEXIONS ========
        Label titreConn = new Label("Gérer les connexions maison ↔ générateur");
        titreConn.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        ComboBox<Maison> cbMaisonConn = new ComboBox<>(maisonsObservable);
        cbMaisonConn.setPromptText("Choisir une maison");

        ComboBox<Generateur> cbGenConn = new ComboBox<>(generateursObservable);
        cbGenConn.setPromptText("Choisir un générateur");
     
        cbMaisonConn.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Maison item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Choisir une maison" : item.getNom());
            }
        });

        cbMaisonConn.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Maison item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });

        cbGenConn.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Generateur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Choisir un générateur" : item.getNom());
            }
        });

        cbGenConn.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Generateur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNom());
            }
        });


        Button btnAjouterConn = new Button("Ajouter / modifier connexion");
        Button btnSupprimerConn = new Button("Supprimer connexion");
        Label lblMsgConn = new Label();

        btnAjouterConn.setOnAction(e -> {
            Maison m = cbMaisonConn.getValue();
            Generateur g = cbGenConn.getValue();

            if (m == null || g == null) {
                lblMsgConn.setText("Veuillez choisir une maison et un générateur.");
                return;
            }

            String saisie = g.getNom() + " " + m.getNom();
            boolean ok = controller.ajouterConnexion(saisie, reseau.getConnexions());

            if (ok) {
                refreshAllViews();
                cbMaisonConn.setValue(null);
                cbGenConn.setValue(null);

                
            } else {
                lblMsgConn.setText("Connexion invalide ou déjà existante.");
            }
        });

        btnSupprimerConn.setOnAction(e -> {
            Maison m = cbMaisonConn.getValue();
            Generateur g = cbGenConn.getValue();

            if (m == null || g == null) {
                lblMsgConn.setText("Veuillez choisir une maison et un générateur.");
                return;
            }

            String saisie = g.getNom() + " " + m.getNom();
            boolean ok = controller.supprimerConnexion(saisie, reseau.getConnexions());

            if (ok) {
                refreshAllViews();
                updateCostLabels();
                lblMsgConn.setText("Connexion supprimée.");
                cbMaisonConn.setValue(null);
                cbGenConn.setValue(null);

            } else {
                lblMsgConn.setText("Cette connexion n'existe pas.");
            }
        });

        HBox boutonsConn = new HBox(btnAjouterConn, btnSupprimerConn);
        boutonsConn.setSpacing(10);

        VBox blocConn = new VBox(titreConn, cbMaisonConn, cbGenConn, boutonsConn, lblMsgConn);
        blocConn.setSpacing(5);
        blocConn.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-padding: 10;");

        root.getChildren().addAll(blocGen, blocMaison, blocConn);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    // =========================================================
    //  ONGLET 3 : COÛT & OPTIMISATION
    // =========================================================
    private VBox buildAnalyseView() {

        VBox root = new VBox();
        root.setSpacing(15);
        root.setPadding(new Insets(15));

        Label titre = new Label("Analyse du coût du réseau");
        titre.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        lblValidite = new Label("Réseau non vérifié.");
        lblValidite.setStyle("-fx-text-fill: #555555;");

        lblMoyenne = new Label("Moyenne utilisation : -");
        lblDispersion = new Label("Dispersion : -");
        lblSurcharge = new Label("Surcharge : -");
        lblCout = new Label("Coût total : -");
        lblLambda = new Label("λ actuel : " + reseau.getLambda());

        Button btnVerif = new Button("Vérifier la validité du réseau");
        btnVerif.setOnAction(e -> reseauValidePourOptimisation());

        Button btnCalculer = new Button("Calculer le coût");
        btnCalculer.setOnAction(e -> updateCostLabels());

        // Changer lambda
        TextField tfLambda = new TextField();
        tfLambda.setPromptText("Nouveau λ (ex: 10)");
        Button btnSetLambda = new Button("Mettre à jour λ");

        btnSetLambda.setOnAction(e -> {
            String s = tfLambda.getText().trim();
            if (s.isEmpty()) return;
            try {
                int lambda = Integer.parseInt(s);
                reseau.setLambda(lambda);
                lblLambda.setText("λ actuel : " + reseau.getLambda());
                updateCostLabels();
            } catch (NumberFormatException ex) {
                tfLambda.setText("");
            }
        });

        HBox lambdaBox = new HBox(new Label("Paramètre λ : "), tfLambda, btnSetLambda);
        lambdaBox.setSpacing(10);

        // ===== Optimisation avec nombre d'itérations fixe =====
     // ===== Optimisation (recuit simulé, arrêt automatique) =====
        Label titreOpt = new Label("Optimisation (recuit simulé)");
        titreOpt.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label lblInfoIter = new Label(
                "L'optimisation s'arrête automatiquement lorsque la température est trop basse\n" +
                "ou lorsqu'aucune amélioration n'est observée depuis un certain temps."
        );

        Button btnOptimiser = new Button("Lancer l'optimisation");
        lblMsgOpt = new Label();

        btnOptimiser.setOnAction(e -> {

            // Vérifier la validité du réseau AVANT d'optimiser
            boolean ok = reseauValidePourOptimisation();
            if (!ok) {
                lblMsgOpt.setText("Réseau invalide : voir les messages de validité ci-dessus.");
                return;
            }

            double avant = reseau.coutTotal();

            // 🔥 NOUVEAU : récupération du nombre d'itérations
            int iterations = Optimiseur.optimiser(reseau);

            refreshAllViews();
            updateCostLabels();

            double apres = reseau.coutTotal();

            lblMsgOpt.setText(String.format(
                    "Optimisation terminée en %d itérations.\nCoût avant : %.3f | après : %.3f",
                    iterations, avant, apres
            ));
        });


        root.getChildren().addAll(
                titre,
                lblValidite,
                lblMoyenne, lblDispersion, lblSurcharge, lblCout, lblLambda,
                btnVerif,
                btnCalculer,
                new Separator(),
                lambdaBox,
                new Separator(),
                titreOpt,
                lblInfoIter,
                btnOptimiser,
                lblMsgOpt
        );

        return root;
    }

    // =========================================================
    //  ONGLET 4 : FICHIERS (IMPORT / SAUVEGARDE / NOUVEAU RESEAU)
    // =========================================================
    private VBox buildFichierView() {

        VBox root = new VBox();
        root.setSpacing(15);
        root.setPadding(new Insets(15));

        Label titre = new Label("Import / Export de réseau (Partie 2)");
        titre.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label lblInfo = new Label("Format attendu : generateur(...). maison(...). connexion(...).");

        Button btnCharger = new Button("Charger un fichier...");
        Button btnSauvegarder = new Button("Sauvegarder la solution actuelle...");
        Button btnNouveau = new Button("Nouveau réseau (réinitialiser)");

        lblFichierActuel = new Label("Aucun fichier chargé.");
        Label lblMsgFile = new Label();

        btnCharger.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choisir un fichier de réseau");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers texte", "*.txt", "*.dat", "*.*")
            );
            File file = chooser.showOpenDialog(root.getScene().getWindow());
            if (file != null) {
                try {
                    Reseau r = ReseauIO.charger(file.getAbsolutePath());
                    this.reseau = r;
                    this.controller = new Controller(reseau);
                    lblFichierActuel.setText("Fichier chargé : " + file.getName());
                    lblMsgFile.setText("Réseau chargé avec succès.");
                    refreshAllViews();
                    updateCostLabels();
                    if (lblMsgOpt != null) lblMsgOpt.setText("");
                } catch (Exception ex) {
                    lblMsgFile.setText("Erreur : " + ex.getMessage());
                }
            }
        });

        btnSauvegarder.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Enregistrer la solution");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")
            );
            File file = chooser.showSaveDialog(root.getScene().getWindow());
            if (file != null) {
                ReseauIO.sauvegarder(reseau, file.getAbsolutePath());
                lblMsgFile.setText("Solution sauvegardée dans : " + file.getName());
            }
        });

        btnNouveau.setOnAction(e -> {
            reseau = new Reseau();
            reseau.setLambda(10);
            controller = new Controller(reseau);
            lblFichierActuel.setText("Aucun fichier chargé.");
            lblMsgFile.setText("Nouveau réseau initialisé.");
            refreshAllViews();
            updateCostLabels();
            if (lblMsgOpt != null) lblMsgOpt.setText("");
            
            
        });

        root.getChildren().addAll(
                titre,
                lblInfo,
                btnCharger,
                btnSauvegarder,
                btnNouveau,
                lblFichierActuel,
                lblMsgFile
        );

        return root;
    }

    // =========================================================
    //  OUTILS INTERNES
    // =========================================================

    /** Rafraîchit toutes les vues à partir du modèle actuel. */
    private void refreshAllViews() {
        maisonsObservable.setAll(reseau.getMaisons());
        generateursObservable.setAll(reseau.getGenerateurs());
        connexionsObservable.setAll(reseau.getConnexions());

        if (tableGenerateurs != null) tableGenerateurs.refresh();
        if (tableMaisons != null) tableMaisons.refresh();

        rebuildConnexionsParGenerateur();
        updateResumeReseau();
    }

    /** Reconstruit l'affichage des connexions par générateur. */
    private void rebuildConnexionsParGenerateur() {
        if (listConnParGenerateur == null) return;

        List<String> lignes = new ArrayList<>();

        List<Generateur> gensTries = new ArrayList<>(reseau.getGenerateurs());
        gensTries.sort(Comparator.comparing(Generateur::getNom, String.CASE_INSENSITIVE_ORDER));

        for (Generateur g : gensTries) {
            double charge = reseau.chargeGenerateur(g);
            double util = reseau.tauxUtilisation(g) * 100.0;
            lignes.add(String.format("%s (C=%d kW, charge=%.1f kW, %.1f%%) :",
                    g.getNom(), g.getCapacite(), charge, util));

            boolean aMaison = false;
            for (Connexion c : reseau.getConnexions()) {
                if (c.getGenerateur().equals(g)) {
                    Maison m = c.getMaison();
                    String type = (m.getType() != null ? TypeMaison.typeToString(m.getType()) : "?");
                    lignes.add(String.format("   - %s (%s, %.1f kW)",
                            m.getNom(), type, m.getConsommation()));
                    aMaison = true;
                }
            }
            if (!aMaison) {
                lignes.add("   (aucune maison connectée)");
            }
            lignes.add(""); // ligne vide entre générateurs
        }

        listConnParGenerateur.setItems(FXCollections.observableArrayList(lignes));
    }


    /** Met à jour le résumé du réseau (compteurs, demande/capacité). */
    private void updateResumeReseau() {
        if (lblResumeReseau == null) return;

        int nbM = reseau.getMaisons().size();
        int nbG = reseau.getGenerateurs().size();
        int nbC = reseau.getConnexions().size();

        double demandeTotale = 0.0;
        for (Maison m : reseau.getMaisons()) {
            demandeTotale += m.getConsommation();
        }

        double capaciteTotale = 0.0;
        for (Generateur g : reseau.getGenerateurs()) {
            capaciteTotale += g.getCapacite();
        }

        lblResumeReseau.setText(String.format(
                "Maisons : %d   |   Générateurs : %d   |   Connexions : %d   |   Demande totale : %.1f kW   |   Capacité totale : %.1f kW",
                nbM, nbG, nbC, demandeTotale, capaciteTotale
        ));
    }

    /** Recalcule les indicateurs de coût et met à jour l'affichage. */
    private void updateCostLabels() {
        if (lblMoyenne == null) return;

        if (reseau.getGenerateurs().isEmpty()) {
            lblMoyenne.setText("Moyenne utilisation : -");
            lblDispersion.setText("Dispersion : -");
            lblSurcharge.setText("Surcharge : -");
            lblCout.setText("Coût total : -");
            lblLambda.setText("λ actuel : " + reseau.getLambda());
        } else {
            double moyenne = reseau.moyenneTauxUtilisation();
            double disp = reseau.dispersion();
            double sur = reseau.surcharge();
            double cout = reseau.coutTotal();

            lblMoyenne.setText(String.format("Moyenne utilisation : %.3f", moyenne));
            lblDispersion.setText(String.format("Dispersion : %.3f", disp));
            lblSurcharge.setText(String.format("Surcharge : %.3f", sur));
            lblCout.setText(String.format("Coût total (λ=%d) : %.3f", reseau.getLambda(), cout));
            lblLambda.setText("λ actuel : " + reseau.getLambda());
        }

        if (tableGenerateurs != null) tableGenerateurs.refresh();
    }

    /**
     * Vérifie si le réseau respecte les conditions de validité (partie 1) :
     * - au moins une maison et un générateur
     * - demande ≤ capacité totale
     * - toutes les maisons sont connectées
     * - aucune maison n'a plusieurs générateurs
     * Met à jour lblValidite et retourne true/false.
     */
    private boolean reseauValidePourOptimisation() {
        if (lblValidite == null) return false;

        StringBuilder sb = new StringBuilder();
        boolean ok = true;

        if (reseau.getMaisons().isEmpty() || reseau.getGenerateurs().isEmpty()) {
            ok = false;
            sb.append("Il faut au moins une maison et un générateur.\n");
        }

        if (!reseau.offreSuperieureOuEgaleADemande()) {
            ok = false;
            sb.append("La demande totale dépasse la capacité totale des générateurs.\n");
        }

        if (!reseau.toutesMaisonsConnectees(reseau.getConnexions())) {
            ok = false;
            sb.append("Certaines maisons ne sont pas connectées à un générateur.\n");
        }

        List<Maison> doublons = controller.verifierConnexions(reseau.getConnexions());
        if (!doublons.isEmpty()) {
            ok = false;
            sb.append("Certaines maisons sont connectées à plusieurs générateurs : ");
            for (int i = 0; i < doublons.size(); i++) {
                sb.append(doublons.get(i).getNom());
                if (i < doublons.size() - 1) sb.append(", ");
            }
            sb.append(".\n");
        }

        if (ok) {
            lblValidite.setText("Réseau VALIDE pour l'optimisation.");
            lblValidite.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            lblValidite.setText("Réseau INVALIDE :\n" + sb.toString());
            lblValidite.setStyle("-fx-text-fill: red;");
        }

        return ok;
    }
}
