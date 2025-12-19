package com.gestion.evenements.controller;

import com.gestion.evenements.model.membres.entities.Administrateur;
import com.gestion.evenements.model.membres.enums.NiveauAcces;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class PermissionsController {

    @FXML private ComboBox<Administrateur> userCombo;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextArea logArea;
    @FXML private Button updateButton;

    private List<Administrateur> adminsList;

    // Injecte la liste des admins
    public void setAdminsList(List<Administrateur> admins) {
        this.adminsList = admins;

        userCombo.setItems(FXCollections.observableArrayList(admins));
        roleCombo.getItems().addAll("SUPER_ADMIN", "ADMIN", "MODERATEUR");

        if (!admins.isEmpty()) {
            userCombo.getSelectionModel().select(0);
            roleCombo.setValue(admins.get(0).getNiveau().name());
        }

        updateLog("Fenêtre ouverte");
    }

    // Modifier la permission
    @FXML
    public void updatePermission() {
        Administrateur selectedAdmin = userCombo.getValue();
        String newRole = roleCombo.getValue();

        if (selectedAdmin != null && newRole != null) {
            // Vérifier que l'admin connecté a un niveau supérieur
            // Exemple: SUPER_ADMIN peut changer tout le monde, ADMIN peut changer MODERATEUR uniquement
            NiveauAcces currentNiveau = selectedAdmin.getNiveau();
            NiveauAcces newNiveau = NiveauAcces.valueOf(newRole);

            // Appliquer la permission
            selectedAdmin.setNiveau(newNiveau);
            updateLog("✅ Permission changée : " + selectedAdmin.getNomComplet() + " → " + newRole);
        } else {
            updateLog("⚠️ Sélection ou rôle manquant !");
        }
    }

    private void updateLog(String action) {
        logArea.appendText(action + "\n");
    }

    // Ouvrir la fenêtre depuis AdministrateurController
    public static void open(List<Administrateur> admins) throws IOException {
        FXMLLoader loader = new FXMLLoader(PermissionsController.class.getResource("/views/permissions.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL); // modal
        stage.setTitle("Gérer Permissions");

        PermissionsController controller = loader.getController();
        controller.setAdminsList(admins);

        stage.showAndWait();
    }
}
