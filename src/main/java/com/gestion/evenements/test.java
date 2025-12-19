package com.gestion.evenements;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class test extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println(getClass().getResource("/fxml/evenements/MenuPrincipal.fxml"));
        try {
            // Charger la page Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Gestion d'Événements - Connexion");
            primaryStage.setScene(scene);
            // Taille par défaut
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.show();
            System.out.println("🎬 Application lancée : page Login chargée.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur de lancement de l'application.");
        }
    }

    public static void main(String[] args) {
        launch(args);

    }
}

