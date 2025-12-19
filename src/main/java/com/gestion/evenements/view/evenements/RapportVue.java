package com.gestion.evenements.view.evenements;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import com.gestion.evenements.controller.evenements.RapportController;
import com.gestion.evenements.controller.evenements.EvenementController;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.records.Rapport;
import
        com.gestion.evenements.model.evenements.records.StatistiquesEvenement;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class RapportVue extends Application implements
        IEvenementVue {
    private RapportController rapportController;
    private EvenementController evenementController;
    private ComboBox<Evenement> cmbEvenement;
    private TextArea txtRapport;
    private PieChart pieChartStatuts;
    private BarChart<String, Number> barChartRevenu;
    @Override
    public void start(Stage primaryStage) {
        rapportController = new RapportController();
        evenementController = new EvenementController();
        primaryStage.setTitle("Rapports et Statistiques");
        BorderPane root = new BorderPane();
        root.setTop(creerBarreOutils());
        root.setCenter(creerZonePrincipale());
        root.setBottom(creerBarreStatut());
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
        chargerEvenements();
    }
    private HBox creerBarreOutils() {
        HBox barre = new HBox(15);

        barre.setPadding(new Insets(15));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #8e44ad;");
        Button btnRapportGlobal = new Button("📊 Rapport Global");
        Button btnRapportEvenement = new Button("📈 Rapport Événement");
                Button btnStatistiques = new Button("📉 Statistiques Détaillées");
                Button btnActualiser = new Button("🔄 Actualiser");
        styliserBouton(btnRapportGlobal, "#9b59b6");
        styliserBouton(btnRapportEvenement, "#3498db");
        styliserBouton(btnStatistiques, "#e74c3c");
        styliserBouton(btnActualiser, "#95a5a6");
        btnRapportGlobal.setOnAction(e -> genererRapportGlobal());
        btnRapportEvenement.setOnAction(e ->
                genererRapportEvenement());
        btnStatistiques.setOnAction(e ->
                afficherStatistiquesDetaillees());
        btnActualiser.setOnAction(e -> actualiserGraphiques());
        barre.getChildren().addAll(
                btnRapportGlobal,
                btnRapportEvenement,
                btnStatistiques,
                new Separator(),
                btnActualiser

        );
        return barre;
    }
    private BorderPane creerZonePrincipale() {
        BorderPane zone = new BorderPane();
// Zone de sélection en haut
        VBox selectionBox = new VBox(10);
        selectionBox.setPadding(new Insets(15));
        selectionBox.setStyle("-fx-background-color: #ecf0f1;");
        Label lblSelection = new Label("Sélectionner un événement pour le rapport détaillé:");
        lblSelection.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        cmbEvenement = new ComboBox<>();

        cmbEvenement.setMaxWidth(Double.MAX_VALUE);
        cmbEvenement.setPromptText("Choisir un événement...");
        selectionBox.getChildren().addAll(lblSelection,
                cmbEvenement);
        zone.setTop(selectionBox);
// Zone centrale avec onglets
        TabPane tabPane = new TabPane();
// Onglet Rapport Texte
        Tab tabRapport = new Tab("📄 Rapport Texte");
        tabRapport.setClosable(false);
        VBox rapportBox = new VBox(10);
        rapportBox.setPadding(new Insets(15));
        txtRapport = new TextArea();
        txtRapport.setEditable(false);
        txtRapport.setWrapText(true);
        txtRapport.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        txtRapport.setText("Cliquez sur un bouton pour générer un rapport...");
        rapportBox.getChildren().add(txtRapport);
        VBox.setVgrow(txtRapport, Priority.ALWAYS);
        tabRapport.setContent(rapportBox);
// Onglet Graphiques
        Tab tabGraphiques = new Tab("📊 Graphiques");
        tabGraphiques.setClosable(false);
        GridPane graphiquesGrid = new GridPane();
        graphiquesGrid.setHgap(15);
        graphiquesGrid.setVgap(15);
        graphiquesGrid.setPadding(new Insets(15));
// Graphique circulaire (PieChart)
        VBox pieBox = new VBox(10);
        Label lblPie = new Label("Répartition des Participants par Événement");
                lblPie.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        pieChartStatuts = new PieChart();
        pieChartStatuts.setTitle("Participants");
        pieChartStatuts.setLegendVisible(true);

        pieBox.getChildren().addAll(lblPie, pieChartStatuts);
// Graphique à barres (BarChart)
        VBox barBox = new VBox(10);
        Label lblBar = new Label("Revenus par Événement");
        lblBar.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Événements");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenu (DT)");
        barChartRevenu = new BarChart<>(xAxis, yAxis);
        barChartRevenu.setTitle("Revenus Générés");
        barChartRevenu.setLegendVisible(false);
        barBox.getChildren().addAll(lblBar, barChartRevenu);
        graphiquesGrid.add(pieBox, 0, 0);
        graphiquesGrid.add(barBox, 1, 0);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        graphiquesGrid.getColumnConstraints().addAll(col1, col2);
        tabGraphiques.setContent(graphiquesGrid);
// Onglet Statistiques
        Tab tabStats = new Tab("📈 Statistiques");
        tabStats.setClosable(false);
        ScrollPane statsScroll = creerPanneauStatistiques();
        tabStats.setContent(statsScroll);
        tabPane.getTabs().addAll(tabRapport, tabGraphiques,
                tabStats);
        zone.setCenter(tabPane);
        return zone;
    }
    private ScrollPane creerPanneauStatistiques() {
        VBox statsBox = new VBox(20);

        statsBox.setPadding(new Insets(20));
        Label titre = new Label("📊 Statistiques Générales");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
// Cartes de statistiques
        GridPane cartes = new GridPane();
        cartes.setHgap(15);
        cartes.setVgap(15);
// Calculer les statistiques
        List<Evenement> evenements =
                evenementController.listerEvenements();
        int totalEvenements = evenements.size();
        int totalParticipants =
                evenements.stream().mapToInt(Evenement::getNbParticipants).sum();
        double revenuTotal =
                evenements.stream().mapToDouble(Evenement::calculerRevenu).sum();
        double tauxMoyen = evenements.isEmpty() ? 0 :
                evenements.stream().mapToDouble(Evenement::getTauxRemplissage).average().orElse(0);
        VBox carteTotalEvenements = creerCarte("🎉", "Total Événements",

                String.valueOf(totalEvenements), "#3498db");
        VBox carteParticipants = creerCarte("👥", "Total Participants",

                String.valueOf(totalParticipants), "#2ecc71");
        VBox carteRevenu = creerCarte("💰", "Revenu Total",
                String.format("%.2f DT", revenuTotal), "#e74c3c");

        VBox carteTauxRemplissage = creerCarte("📊", "Taux Remplissage Moyen",

                String.format("%.1f%%", tauxMoyen), "#f39c12");

        cartes.add(carteTotalEvenements, 0, 0);
        cartes.add(carteParticipants, 1, 0);
        cartes.add(carteRevenu, 0, 1);
        cartes.add(carteTauxRemplissage, 1, 1);
        for (int i = 0; i < 2; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(50);
            cartes.getColumnConstraints().add(col);
        }
        statsBox.getChildren().addAll(titre, cartes);

        ScrollPane scroll = new ScrollPane(statsBox);
        scroll.setFitToWidth(true);
        return scroll;
    }
    private VBox creerCarte(String icone, String titre, String
            valeur, String couleur) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(20));
        carte.setStyle(

                "-fx-background-color: white;" +

                        "-fx-border-color: " + couleur + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"

        );
        carte.setAlignment(Pos.CENTER);
        carte.setPrefHeight(150);
        Label lblIcone = new Label(icone);
        lblIcone.setStyle("-fx-font-size: 40px;");
        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-font-size: 14px; -fx-text-fill:#7f8c8d;");
        Label lblValeur = new Label(valeur);
        lblValeur.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + couleur + ";");
        carte.getChildren().addAll(lblIcone, lblTitre, lblValeur);
        return carte;
    }
    private HBox creerBarreStatut() {
        HBox barre = new HBox(10);
        barre.setPadding(new Insets(10));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #2c3e50;");
        Label lblStatut = new Label("Prêt à générer des rapports");
        lblStatut.setStyle("-fx-text-fill: white;");
        barre.getChildren().add(lblStatut);
        return barre;
    }

    private void styliserBouton(Button btn, String couleur) {
        btn.setStyle(

                "-fx-background-color: " + couleur + ";" +

                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;"

        );
    }
    private void chargerEvenements() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
        cmbEvenement.setItems(FXCollections.observableArrayList(evenements)
        );
    }
    private void genererRapportGlobal() {
        try {
            Rapport rapport =

                    rapportController.genererRapportGlobal();
            afficherRapport(rapport);
            actualiserGraphiques();
            afficherMessage("Rapport global généré avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }
    private void genererRapportEvenement() {
        Evenement evenement = cmbEvenement.getValue();
        if (evenement == null) {
            afficherErreur("Veuillez sélectionner un événement");
            return;
        }
        try {
            Rapport rapport =

                    rapportController.genererRapportEvenement(evenement.getId());

            afficherRapport(rapport);
            StatistiquesEvenement stats =

                    rapportController.calculerStatistiquesEvenement(evenement.getId())
                    ;

            if (stats != null) {
                afficherStatistiquesEvenement(stats);

            }
            afficherMessage("Rapport de l'événement généré avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur lors de la génération du rapport: " + e.getMessage());
        }
    }
    private void afficherStatistiquesDetaillees() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
                sb.append(" STATISTIQUES DÉTAILLÉES DES ÉVÉNEMENTS\n");
                        sb.append("═══════════════════════════════════════════════════════\n\n");
        for (Evenement evt : evenements) {
            StatistiquesEvenement stats =

                    rapportController.calculerStatistiquesEvenement(evt.getId());

            if (stats != null) {
                sb.append("📅").append(evt.getTitre()).append("\n");
                        sb.append(" Lieu:").append(evt.getLieu()).append("\n");
                                sb.append(" Date:").append(evt.getDateDebut().format(DateTimeFormatter.ofPattern("dd /MM/yyyy HH:mm"))).append("\n");

                sb.append(" Participants:").append(stats.nbInscrits()).append("/").append(stats.capaciteMax
                ()).append("\n");

                sb.append(" Taux de remplissage:").append(String.format("%.1f%%",
                        stats.tauxRemplissage())).append("\n");

                sb.append(" Revenu: ").append(String.format("%.2f DT", stats.revenu())).append("\n");

                        sb.append(" Inscriptions:\n");
                sb.append(" - Confirmées ").append(stats.nbConfirmees()).append("\n");

                        sb.append(" - En attente:").append(stats.nbEnAttente()).append("\n");
                                sb.append(" - Annulées:").append(stats.nbAnnulees()).append("\n");

                                        sb.append("\n");
            }
        }
        txtRapport.setText(sb.toString());
    }
    private void afficherRapport(Rapport rapport) {
        if (rapport == null) {
            txtRapport.setText("Aucune donnée disponible.");
            return;
        }
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
                sb.append("").append(rapport.titre()).append("\n");
                        sb.append("═══════════════════════════════════════════════════════\n");
                                sb.append("Généré le :").append(rapport.dateGeneration().format(formatter)).append("\n")

        .append("───────────────────────────────────────────────────────\n\n");
                sb.append("📊 RÉSUMÉ\n");
        sb.append(" • Nombre d'événements :").append(rapport.nbEvenements()).append("\n");
                sb.append(" • Nombre de participants :").append(rapport.nbParticipants()).append("\n");
                        sb.append(" • Revenu total : ").append(String.format("%.2f DT", rapport.revenuTotal())).append("\n\n");
                                sb.append("───────────────────────────────────────────────────────\n");
                                        sb.append("📝 DÉTAILS\n\n");
        sb.append(rapport.contenu());

        sb.append("\n═══════════════════════════════════════════════════════\n");
                txtRapport.setText(sb.toString());
    }
    private void
    afficherStatistiquesEvenement(StatistiquesEvenement stats) {
// Mise à jour du graphique circulaire
        pieChartStatuts.getData().clear();
        pieChartStatuts.getData().add(new PieChart.Data("Confirmées (" + stats.nbConfirmees() + ")", stats.nbConfirmees()));
        pieChartStatuts.getData().add(new PieChart.Data("En attente (" + stats.nbEnAttente() + ")", stats.nbEnAttente()));
        pieChartStatuts.getData().add(new PieChart.Data("Annulées (" + stats.nbAnnulees() + ")", stats.nbAnnulees()));
    }
    private void actualiserGraphiques() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
// Mise à jour du graphique à barres
        XYChart.Series<String, Number> series = new
                XYChart.Series<>();
        series.setName("Revenus");
        for (Evenement evt : evenements) {
            double revenu = evt.calculerRevenu();
            String titreAbrege = evt.getTitre().length() > 15 ?
                    evt.getTitre().substring(0, 12) + "..." :

                    evt.getTitre();

            series.getData().add(new XYChart.Data<>(titreAbrege,

                    revenu));
        }
        barChartRevenu.getData().clear();
        barChartRevenu.getData().add(series);
// Mise à jour du graphique circulaire (participants)
        pieChartStatuts.getData().clear();
        for (Evenement evt : evenements) {
            if (evt.getNbParticipants() > 0) {
                pieChartStatuts.getData().add(

                        new PieChart.Data(evt.getTitre() + " (" +

                                evt.getNbParticipants() + ")",

                                evt.getNbParticipants())

                );
            }
        }
    }
    @Override
    public void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Override
    public void afficherErreur(String erreur) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(erreur);
        alert.showAndWait();
    }
}