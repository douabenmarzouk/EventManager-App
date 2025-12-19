package com.gestion.evenements.view.evenements;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {
    @FXML
    private Button btnGererEvenements;
    @FXML
    private Button btnGererInscriptions;
    @FXML
    private Button btnRapports;
    @FXML
    private Button btnQuitter;
    @FXML
    private void ouvrirGestionEvenements() {
        try {
            FXMLLoader loader = new

                    FXMLLoader(getClass().getResource("/fxml.evenements/EvenementVue.fxml"));

                    Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Événements");
            stage.setScene(new Scene(root, 1200, 700));
            stage.show();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture : " +

                    e.getMessage());

            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirGestionInscriptions() {

        try {
            FXMLLoader loader = new

                    FXMLLoader(getClass().getResource("/fxml.evenements/InscriptionVue.fxml"));

                    Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Inscriptions");
            stage.setScene(new Scene(root, 1200, 700));
            stage.show();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture : " +

                    e.getMessage());

            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirRapports() {
        try {
            FXMLLoader loader = new

                    FXMLLoader(getClass().getResource("/fxml.evenements/RapportVue.fxml"));

                    Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Rapports et Statistiques");
            stage.setScene(new Scene(root, 1200, 700));
            stage.show();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'ouverture : " +

                    e.getMessage());

            e.printStackTrace();
        }
    }
    @FXML
    private void quitter() {
        try {
            // Charger la vue Administrateur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdministrateurView.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre actuelle
            Stage stage = (Stage) btnQuitter.getScene().getWindow();

            // Remplacer la scène actuelle
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Administrateur");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void afficherErreur(String erreur) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(erreur);
        alert.showAndWait();

    }
}
