package com.gestion.evenements.view.evenements;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static java.awt.Color.white;
import static java.awt.SystemColor.text;


public class MenuEvenements extends Application implements
        IEvenementVue {
    private EvenementController evenementController;
    private InscriptionController inscriptionController;
    private RapportController rapportController;
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
// Initialisation des contrôleurs
        evenementController = new EvenementController();
        inscriptionController = new InscriptionController();
        rapportController = new RapportController();
        primaryStage.setTitle("Gestion des Événements - Menu Principal");
                primaryStage.setScene(creerSceneMenu());
        primaryStage.show();
    }
    private Scene creerSceneMenu() {
        VBox root = new VBox(20);

        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
// Titre
        Label titre = new Label("🎉 GESTION DES ÉVÉNEMENTS");
        titre.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label sousTitre = new Label("Système de Gestion des Événements Associatifs");
                sousTitre.setStyle("-fx-font-size: 14px; -fx-text-fill:#7f8c8d;");
// Boutons principaux
        Button btnGererEvenements = creerBoutonMenu("📅 Gérer les Événements", "#3498db");
        Button btnGererInscriptions = creerBoutonMenu("✍️ Gérer les Inscriptions", "#2ecc71");
        Button btnRapports = creerBoutonMenu("📊 Rapports et Statistiques", "#e74c3c");
                Button btnQuitter = creerBoutonMenu("🚪 Quitter",
                        "#95a5a6");
// Actions des boutons
        btnGererEvenements.setOnAction(e ->
                ouvrirGestionEvenements());
        btnGererInscriptions.setOnAction(e ->
                ouvrirGestionInscriptions());
        btnRapports.setOnAction(e -> ouvrirRapports());
        btnQuitter.setOnAction(e -> primaryStage.close());
        root.getChildren().addAll(

                titre,
                sousTitre,
                new Label(""), // Espace
                btnGererEvenements,
                btnGererInscriptions,
                btnRapports,
                new Label(""), // Espace
                btnQuitter

        );
        return new Scene(root, 600, 500);
    }
    private Button creerBoutonMenu(String texte, String couleur) {
        Button btn = new Button(texte);

        btn.setPrefWidth(350);
        btn.setPrefHeight(50);
        btn.setStyle(

                "-fx-background-color: " + couleur + ";" +

                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"

        );
        btn.setOnMouseEntered(e ->
                btn.setStyle(
                        "-fx-background-color: derive(" + couleur + ", -20%);" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 16px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;" +
                                "-fx-scale-x: 1.05;" +
                                "-fx-scale-y: 1.05;"
                )
        );

        btn.setOnMouseExited(e -> btn.setStyle(

                "-fx-background-color: " + couleur + ";" +

                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"

        ));
        return btn;
    }
    private void ouvrirGestionEvenements() {
        EvenementVue evenementVue = new EvenementVue();
        Stage stage = new Stage();
        try {
            evenementVue.start(stage);
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture de la gestion des événements: " + e.getMessage());
        }
    }
    private void ouvrirGestionInscriptions() {
        InscriptionVue inscriptionVue = new InscriptionVue();

        Stage stage = new Stage();
        try {
            inscriptionVue.start(stage);
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture de la gestion des inscriptions: " + e.getMessage());
        }
    }
    private void ouvrirRapports() {
        RapportVue rapportVue = new RapportVue();
        Stage stage = new Stage();
        try {
            rapportVue.start(stage);
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture des rapports: " + e.getMessage());
        }
    }
    @Override
    public void afficherMessage(String message) {
        javafx.scene.control.Alert alert = new
                javafx.scene.control.Alert(

                javafx.scene.control.Alert.AlertType.INFORMATION

        );
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Override
    public void afficherErreur(String erreur) {
        javafx.scene.control.Alert alert = new
                javafx.scene.control.Alert(

                javafx.scene.control.Alert.AlertType.ERROR

        );
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(erreur);
        alert.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}