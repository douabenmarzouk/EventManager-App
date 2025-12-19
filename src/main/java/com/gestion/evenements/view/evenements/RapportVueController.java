package com.gestion.evenements.view.evenements;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.gestion.evenements.controller.evenements.RapportController;
import com.gestion.evenements.controller.evenements.EvenementController;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.records.Rapport;
import com.gestion.evenements.model.evenements.records.StatistiquesEvenement;
import java.time.format.DateTimeFormatter;

import java.util.List;

public class RapportVueController {
    @FXML private ComboBox<Evenement> cmbEvenement;
    @FXML private TextArea txtRapport;
    @FXML private PieChart pieChartStatuts;
    @FXML private BarChart<String, Number> barChartRevenu;
    @FXML private VBox vboxStatistiques;
    private RapportController rapportController;
    private EvenementController evenementController;
    @FXML
    public void initialize() {
        rapportController = new RapportController();
        evenementController = new EvenementController();
        chargerEvenements();
        initialiserStatistiques();
    }
    private void chargerEvenements() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
        cmbEvenement.setItems(FXCollections.observableArrayList(evenements)
        );
    }
    private void initialiserStatistiques() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
        int totalEvenements = evenements.size();
        int totalParticipants =
                evenements.stream().mapToInt(Evenement::getNbParticipants).sum();
        double revenuTotal =
                evenements.stream().mapToDouble(Evenement::calculerRevenu).sum();
        double tauxMoyen = evenements.isEmpty() ? 0 :
                evenements.stream().mapToDouble(Evenement::getTauxRemplissage).average().orElse(0);
// Ajouter les cartes de statistiques
        vboxStatistiques.getChildren().clear();
        javafx.scene.layout.GridPane cartes = new
                javafx.scene.layout.GridPane();

        cartes.setHgap(15);
        cartes.setVgap(15);
        VBox carte1 = creerCarte("🎉", "Total Événements",
                String.valueOf(totalEvenements), "#3498db");
        VBox carte2 = creerCarte("👥", "Total Participants",
                String.valueOf(totalParticipants), "#2ecc71");
        VBox carte3 = creerCarte("💰", "Revenu Total",
                String.format("%.2f DT", revenuTotal), "#e74c3c");
        VBox carte4 = creerCarte("📊", "Taux Remplissage Moyen",
                String.format("%.1f%%", tauxMoyen), "#f39c12");
        cartes.add(carte1, 0, 0);
        cartes.add(carte2, 1, 0);
        cartes.add(carte3, 0, 1);
        cartes.add(carte4, 1, 1);
        javafx.scene.layout.ColumnConstraints col1 = new
                javafx.scene.layout.ColumnConstraints();
        col1.setPercentWidth(50);
        javafx.scene.layout.ColumnConstraints col2 = new
                javafx.scene.layout.ColumnConstraints();
        col2.setPercentWidth(50);
        cartes.getColumnConstraints().addAll(col1, col2);
        vboxStatistiques.getChildren().add(cartes);
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
    @FXML
    private void genererRapportGlobal() {
        try {
            Rapport rapport =

                    rapportController.genererRapportGlobal();
            afficherRapport(rapport);
            actualiserGraphiques();
            afficherMessage("Rapport global généré avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur: " + e.getMessage());
        }
    }
    @FXML
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
            afficherErreur("Erreur: " + e.getMessage());
        }
    }

    @FXML
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
                sb.append(" - Confirmées:").append(stats.nbConfirmees()).append("\n");
                        sb.append(" - En attente:").append(stats.nbEnAttente()).append("\n");
                                sb.append(" - Annulées:").append(stats.nbAnnulees()).append("\n");

                                sb.append("\n");
            }
        }
        txtRapport.setText(sb.toString());

    }
    @FXML
    private void actualiserGraphiques() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
// Graphique à barres
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
// Graphique circulaire
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
        ;
        sb.append("───────────────────────────────────────────────────────\n\n");
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
        pieChartStatuts.getData().clear();
        pieChartStatuts.getData().add(new PieChart.Data("Confirmées (" + stats.nbConfirmees() + ")", stats.nbConfirmees()));
        pieChartStatuts.getData().add(new PieChart.Data("En attente (" + stats.nbEnAttente() + ")", stats.nbEnAttente()));
        pieChartStatuts.getData().add(new PieChart.Data("Annulées (" + stats.nbAnnulees() + ")", stats.nbAnnulees()));
    }
    private void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();

    }
    private void afficherErreur(String erreur) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(erreur);
        alert.showAndWait();
    }
}
