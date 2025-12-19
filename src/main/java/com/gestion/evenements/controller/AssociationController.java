package com.gestion.evenements.controller;

import com.gestion.evenements.database.dao.AssociationDAO;
import com.gestion.evenements.database.dao.FeedbackDAO;
import com.gestion.evenements.database.dao.MembreDAO;
import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.exceptions.MembreException;
import com.gestion.evenements.model.membres.record.Feedback;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class AssociationController {

    private static AssociationController instance;
    @FXML
    private VBox rootVBox;

    // ==================== FORMULAIRE ====================
    @FXML private TextField nomField, emailField, localisationField, budgetField;
    @FXML private DatePicker dateCreationPicker;
    @FXML private CheckBox activeCheckBox;
    @FXML private TextArea descriptionArea;

    // ==================== STATS ====================
    @FXML private Label soldeLabel, membresLabel, cotisationsLabel, feedbacksLabel;

    // ==================== TABLE MEMBRES ====================
    @FXML private TableView<Membre> membresTable;
    @FXML private TableColumn<Membre, Integer> membreIdCol;
    @FXML private TableColumn<Membre, String> membreNumeroCol, membreNomCol, membrePrenomCol, membreEmailCol, membreStatutCol;
    @FXML private TableColumn<Membre, Boolean> membreCotisationCol;
    @FXML private TextField searchMembreField;

    // ==================== TABLE FEEDBACKS ====================
    @FXML private TableView<Feedback> feedbacksTable;
    @FXML private TableColumn<Feedback, String> feedbackMembreCol;
    @FXML private TableColumn<Feedback, Integer> feedbackNoteCol;
    @FXML private TableColumn<Feedback, String> feedbackCommentaireCol;
    @FXML private TableColumn<Feedback, String> feedbackDateCol;

    private Association currentAssociation;

    // ==================== INITIALISATION ====================
    @FXML
    public void initialize() {
        instance = this;
        setupMembresTable();
        setupFeedbacksTable();
        loadDefaultAssociation();
        searchMembreField.textProperty().addListener((o, old, newVal) -> filterMembres());
    }

    public static void refreshFeedbacksIfOpen() {
        if (instance != null && instance.currentAssociation != null) {
            Platform.runLater(() -> {
                try {
                    AssociationDAO associationDAO = new AssociationDAO();
                    FeedbackDAO feedbackDAO = new FeedbackDAO();

                    // Recharger l'association + membres + solde
                    Association refreshed = associationDAO.findByIdAvecMembres(instance.currentAssociation.getId());
                    if (refreshed == null) return;

                    // Mettre à jour membres et solde
                    instance.currentAssociation.getMembres().setAll(refreshed.getMembres());
                    instance.currentAssociation.setSolde(refreshed.getSolde());

                    // CHARGER LES FEEDBACKS DEPUIS LA BASE (maintenant ça marche !)
                    List<Feedback> allFeedbacks = feedbackDAO.getTousPourAssociation(instance.currentAssociation.getId());

                    // Mettre à jour le tableau
                    instance.feedbacksTable.getItems().setAll(allFeedbacks);

                    // Stats
                    instance.soldeLabel.setText(String.format("%.2f €", refreshed.getSolde()));
                    instance.feedbacksLabel.setText(String.valueOf(allFeedbacks.size()));
                    instance.cotisationsLabel.setText(String.valueOf(refreshed.getNombreMembresPayes()));
                    instance.membresLabel.setText(String.valueOf(refreshed.getNombreTotalMembres()));

                    System.out.println("ADMIN RAFRAÎCHI ! " + allFeedbacks.size() + " feedbacks | Solde = " + refreshed.getSolde() + " €");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    @FXML
    private void onActualiser() {
        loadAssociationData(); // recharge toutes les données (formulaire + tables + stats)
    }
    @FXML
    private void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous devrez vous reconnecter pour accéder à l'application.");

        // Récupérer le Stage depuis rootVBox
        Stage stage = (Stage) rootVBox.getScene().getWindow();
        alert.initOwner(stage);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
                    Parent root = loader.load();

                    stage.setScene(new Scene(root));
                    stage.setTitle("Gestion d'Événements - Connexion");
                    stage.setWidth(1000);
                    stage.setHeight(700);
                    stage.centerOnScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de connexion !");
                    error.showAndWait();
                }
            }
        });
    }

    private void setupMembresTable() {
        membreIdCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getId()));
        membreNumeroCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroMembre()));
        membreNomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        membrePrenomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrenom()));
        membreEmailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        membreStatutCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getStatut() != null ? cell.getValue().getStatut().getLibelle() : "Inconnu"
                )
        );
        membreCotisationCol.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().isCotisationPayee())
        );
    }

    private void setupFeedbacksTable() {
        feedbackMembreCol.setCellValueFactory(cell -> {
            Feedback fb = cell.getValue();
            Membre m = currentAssociation.getMembres().stream()
                    .filter(mem -> mem.getId() == fb.idMembre())
                    .findFirst()
                    .orElse(null);
            String nom = m != null ? m.getNomComplet() : "Membre supprimé";
            return new SimpleStringProperty(nom);
        });

        feedbackNoteCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().note()));
        feedbackCommentaireCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().commentaire()));
        feedbackDateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().date().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ))
        );

        feedbacksTable.setItems(FXCollections.observableArrayList());
    }

    private void loadDefaultAssociation() {
        currentAssociation = new Association();
        currentAssociation.setId(1);
        currentAssociation.setNom("Association Tech Students");
        currentAssociation.setDescription("Association des étudiants en technologie");
        currentAssociation.setLocalisation("Bizerte, Tunisie");
        currentAssociation.setEmail("contact@techstudents.tn");
        currentAssociation.setDateCreation(LocalDate.now()); // ← LocalDate, pas Date !
        currentAssociation.setBudget(10000.0);
        currentAssociation.setActive(true);
        currentAssociation.setSolde(0.0);

        try {
            currentAssociation.ajouterMembre(new Membre(1, "Dupont", "Jean", "jean@email.com", "0612345678", false));
            currentAssociation.ajouterMembre(new Membre(2, "Martin", "Marie", "marie@email.com", "0698765432", true));
        } catch (MembreException ignored) {}

        loadAssociationData();
    }

    private void loadAssociationData() {
        if (currentAssociation == null) return;

        // === Formulaire ===
        nomField.setText(currentAssociation.getNom());
        emailField.setText(currentAssociation.getEmail());
        localisationField.setText(currentAssociation.getLocalisation());
        budgetField.setText(String.valueOf(currentAssociation.getBudget()));
        activeCheckBox.setSelected(currentAssociation.isActive());
        descriptionArea.setText(currentAssociation.getDescription());

        // Correction : LocalDate → DatePicker
        if (currentAssociation.getDateCreation() != null) {
            dateCreationPicker.setValue(currentAssociation.getDateCreation());
        }

        // === Stats ===
        soldeLabel.setText(String.format("%.2f €", currentAssociation.getSolde()));
        membresLabel.setText(String.valueOf(currentAssociation.getNombreTotalMembres()));
        cotisationsLabel.setText(String.valueOf(currentAssociation.getNombreMembresPayes()));
        feedbacksLabel.setText(String.valueOf(currentAssociation.getNombreFeedbacks()));

        // === TableView membres (lien direct) ===
        membresTable.setItems(currentAssociation.getMembres());

        // === TableView feedbacks ===
        feedbacksTable.getItems().setAll(currentAssociation.getAllFeedbacks());
    }

    private void filterMembres() {
        String txt = searchMembreField.getText().toLowerCase().trim();
        if (txt.isEmpty()) {
            membresTable.setItems(currentAssociation.getMembres());
        } else {
            var filtered = currentAssociation.getMembres().filtered(m ->
                    m.getNom().toLowerCase().contains(txt) ||
                            m.getPrenom().toLowerCase().contains(txt) ||
                            m.getEmail().toLowerCase().contains(txt)
            );
            membresTable.setItems(filtered);
        }
    }

    // ==================== ACTIONS ====================

    @FXML private void onEnregistrer() {
        try {
            currentAssociation.setNom(nomField.getText().trim());
            currentAssociation.setDescription(descriptionArea.getText());
            currentAssociation.setLocalisation(localisationField.getText().trim());
            currentAssociation.setEmail(emailField.getText().trim());
            currentAssociation.setBudget(Double.parseDouble(budgetField.getText()));
            currentAssociation.setActive(activeCheckBox.isSelected());

            LocalDate date = dateCreationPicker.getValue();
            if (date != null) {
                currentAssociation.setDateCreation(dateCreationPicker.getValue()); // LocalDate direct // ← LocalDate direct !
            }

            loadAssociationData();
            showSuccess("Association enregistrée avec succès !");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }
    // Ajoute cette méthode publique dans AssociationController
    public void setAssociation(Association association) {
        this.currentAssociation = association;
        loadAssociationData(); // recharge tout (formulaire + tables + stats)
    }
    @FXML private void onSupprimerMembre() {
        Membre selected = membresTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un membre");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer définitivement " + selected.getNomComplet() + " ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    currentAssociation.supprimerMembre(selected.getId()); // ← on passe l'ID maintenant !
                    loadAssociationData(); // ou juste refresh stats
                    showSuccess("Membre supprimé");
                } catch (MembreException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    public void refreshAfterMembreChange() {
        loadAssociationData(); // plus complet et sûr que juste 2 labels
    }

    @FXML private void onAjouterMembre() { openMembreForm(null); }
    @FXML private void onModifierMembre() {
        Membre m = membresTable.getSelectionModel().getSelectedItem();
        if (m == null) showError("Sélectionnez un membre");
        else openMembreForm(m);
    }

    private void openMembreForm(Membre membre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AjouterMembreView.fxml"));
            Parent root = loader.load();
            AjouterMembreController ctrl = loader.getController();
            ctrl.setData(currentAssociation, this, membre);

            Stage stage = new Stage();
            stage.setTitle(membre == null ? "Ajouter un membre" : "Modifier " + membre.getNomComplet());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Après fermeture → on recharge tout
            loadAssociationData();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir le formulaire");
        }
    }

    // ==================== ALERTES ====================
    private void showSuccess(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).show();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).show();
    }
}