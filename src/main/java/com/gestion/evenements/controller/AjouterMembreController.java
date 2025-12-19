package com.gestion.evenements.controller;

import com.gestion.evenements.database.dao.MembreDAO;
import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.entities.Membre;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;

public class AjouterMembreController {

    @FXML private TextField nomField, prenomField, emailField, telephoneField;
    @FXML private CheckBox cotisationCheckBox;

    private Association association;
    private AssociationController parentController;
    private Membre membreExistant = null; // null = ajout, non-null = modification

    // Appelée par AssociationController
    public void setData(Association association, AssociationController parent, Membre membre) {
        this.association = association;
        this.parentController = parent;
        this.membreExistant = membre;

        if (membre != null) {
            // Mode modification
            chargerMembre(membre);
        }
    }

    private void chargerMembre(Membre m) {
        nomField.setText(m.getNom());
        prenomField.setText(m.getPrenom());
        emailField.setText(m.getEmail());
        telephoneField.setText(m.getTelephone());
        cotisationCheckBox.setSelected(m.isCotisationPayee());
    }

    @FXML
    private void onEnregistrer() {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String tel = telephoneField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                showAlert("Erreur", "Champs obligatoires manquants !", Alert.AlertType.ERROR);
                return;
            }

            MembreDAO membreDAO = new MembreDAO();

            if (membreExistant == null) {
                Membre nouveau = new Membre(0, nom, prenom, email, tel, cotisationCheckBox.isSelected());

                // ✅ Sauvegarder en BD
                membreDAO.creer(nouveau, "motdepasse123"); // TODO: demander le mot de passe
                membreDAO.ajouterAAssociation(nouveau.getId(), association.getId());
                association.ajouterMembre(nouveau);

                showAlert("Succès", "Membre ajouté !", Alert.AlertType.INFORMATION);
            } else {
                // === MODIFICATION ===
                membreExistant.setNom(nom);
                membreExistant.setPrenom(prenom);
                membreExistant.setEmail(email);
                membreExistant.setTelephone(tel);
                membreExistant.setCotisationPayee(cotisationCheckBox.isSelected());
                membreDAO.mettreAJour(membreExistant);

                showAlert("Succès", "Membre modifié !", Alert.AlertType.INFORMATION);
            }

            parentController.refreshAfterMembreChange();
            fermerFenetre();

        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onAnnuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        ((Stage) nomField.getScene().getWindow()).close();
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}