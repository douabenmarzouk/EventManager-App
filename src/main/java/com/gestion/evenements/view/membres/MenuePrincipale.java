package com.gestion.evenements.view.membres;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

public class MenuePrincipale {

    @FXML private Button btnEvenements;
    @FXML private Button btnMembres;
    @FXML private Button btnInscriptions;
    @FXML private Button btnRapports;
    @FXML private Button btnQuitter;

    @FXML
    private void afficherEvenements(ActionEvent e) {
        System.out.println("👉 Affichage des événements");
    }

    @FXML
    private void afficherMembres(ActionEvent e) {
        System.out.println("👉 Affichage des membres");
    }

    @FXML
    private void afficherInscriptions(ActionEvent e) {
        System.out.println("👉 Affichage des inscriptions");
    }

    @FXML
    private void afficherRapports(ActionEvent e) {
        System.out.println("👉 Affichage des rapports");
    }

    @FXML
    private void quitter(ActionEvent e) {
        System.exit(0);
    }
}
