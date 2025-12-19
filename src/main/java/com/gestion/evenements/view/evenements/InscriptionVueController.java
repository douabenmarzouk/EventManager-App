package com.gestion.evenements.view.evenements;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.gestion.evenements.controller.evenements.InscriptionController;
import com.gestion.evenements.controller.evenements.EvenementController;
import javafx.scene.control.cell.PropertyValueFactory;
import com.gestion.evenements.model.evenements.entities.Inscription;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.enums.StatutInscription;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.awt.Color.white;

public class InscriptionVueController {
    @FXML private TableView<Inscription> tableInscriptions;
    @FXML private TableColumn<Inscription, Integer> colId;
    @FXML private TableColumn<Inscription, Integer> colMembreId;
    @FXML private TableColumn<Inscription, Integer> colEvenementId;
    @FXML private TableColumn<Inscription, LocalDateTime>
            colDateInscription;
    @FXML private TableColumn<Inscription, StatutInscription>
            colStatut;
    @FXML private TableColumn<Inscription, Double> colMontant;
    @FXML private TableColumn<Inscription, Boolean> colConfirmed;
    @FXML private TextField txtId;
    @FXML private TextField txtMembreId;
    @FXML private ComboBox<Evenement> cmbEvenement;
    @FXML private Label lblDateInscription;
    @FXML private ComboBox<StatutInscription> cmbStatut;
    @FXML private TextField txtMontant;
    @FXML private CheckBox chkConfirmed;
    private InscriptionController inscriptionController;
    private EvenementController evenementController;
    private ObservableList<Inscription> inscriptionsList;
    @FXML
    public void initialize() {
        inscriptionController = new InscriptionController();
        evenementController = new EvenementController();
        inscriptionsList = FXCollections.observableArrayList();
// Configuration des colonnes

        colId.setCellValueFactory(new
                PropertyValueFactory<>("id"));
        colMembreId.setCellValueFactory(new
                PropertyValueFactory<>("membreId"));
        colEvenementId.setCellValueFactory(new
                PropertyValueFactory<>("evenementId"));
        colDateInscription.setCellValueFactory(new
                PropertyValueFactory<>("dateInscription"));
        colStatut.setCellValueFactory(new
                PropertyValueFactory<>("statut"));
        colMontant.setCellValueFactory(new
                PropertyValueFactory<>("montantPaye"));
        colConfirmed.setCellValueFactory(new
                PropertyValueFactory<>("confirmed"));
// Format date
        colDateInscription.setCellFactory(col -> new
                TableCell<Inscription, LocalDateTime>() {

                    @Override
                    protected void updateItem(LocalDateTime date, boolean

                            empty) {

                        super.updateItem(date, empty);
                        if (empty || date == null) {
                            setText(null);
                        } else {

                            setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                        }
                    }
                });
// Format montant
        colMontant.setCellFactory(col -> new TableCell<Inscription,
                Double>() {

            @Override
            protected void updateItem(Double montant, boolean

                    empty) {

                super.updateItem(montant, empty);
                if (empty || montant == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f DT", montant));
                }
            }
        });
// Format confirmé

        colConfirmed.setCellFactory(col -> new
                TableCell<Inscription, Boolean>() {

                    @Override
                    protected void updateItem(Boolean confirmed, boolean

                            empty) {

                        super.updateItem(confirmed, empty);
                        if (empty || confirmed == null) {
                            setText(null);
                        } else {
                            setText(confirmed ? "✅ Oui" : "❌ Non");
                        }
                    }
                });
// Couleurs pour statuts
        colStatut.setCellFactory(col -> new TableCell<Inscription,
                StatutInscription>() {
            @Override
            protected void updateItem(StatutInscription statut,

                                      boolean empty) {

                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut.toString());
                    switch (statut) {
                        case EN_ATTENTE:
                            setStyle("-fx-background-color:#f39c12; -fx-text-fill: white;");
                            break;
                        case CONFIRMEE:
                            setStyle("-fx-background-color:#3498db; -fx-text-fill: white;");
                            break;
                        case VALIDEE:
                            setStyle("-fx-background-color:#27ae60; -fx-text-fill: white;");
                            break;
                        case ANNULEE:
                            setStyle("-fx-background-color:#e74c3c; -fx-text-fill: white;");
                            break;

                    }
                }
            }
        });

        tableInscriptions.setItems(inscriptionsList);

        tableInscriptions.getSelectionModel().selectedItemProperty().addListener(

                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherInscriptionDansFormulaire(newSelection);

                    }
                }
        );
// Initialiser ComboBox
        cmbStatut.setItems(FXCollections.observableArrayList(StatutInscription.values()));
        cmbStatut.setValue(StatutInscription.EN_ATTENTE);
        chargerInscriptions();
        chargerEvenements();
    }
    @FXML
    private void chargerInscriptions() {
        List<Inscription> inscriptions =
                inscriptionController.listerInscriptions();
        inscriptionsList.clear();
        inscriptionsList.addAll(inscriptions);
    }
    private void chargerEvenements() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
        cmbEvenement.setItems(FXCollections.observableArrayList(evenements)
        );
    }
    @FXML
    private void nouvelleInscription() {
        viderFormulaire();
        txtMembreId.requestFocus();
    }
    @FXML
    private void validerInscription() {

        Inscription selected =
                tableInscriptions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Veuillez sélectionner une inscription à valider");

            return;
        }
        try {
            inscriptionController.validerInscription(selected.getId());

            chargerInscriptions();
            afficherMessage("Inscription validée avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur: " + e.getMessage());
        }
    }
    @FXML
    private void confirmerInscription() {
        Inscription selected =
                tableInscriptions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Veuillez sélectionner une inscription à confirmer");
            return;
        }
        try {
            inscriptionController.confirmerInscription(selected.getId());

            chargerInscriptions();
            afficherMessage("Inscription confirmée avec succès");
        } catch (Exception e) {
            afficherErreur("Erreur: " + e.getMessage());
        }
    }
    @FXML
    private void annulerInscription() {
        Inscription selected =
                tableInscriptions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Veuillez sélectionner une inscription à annuler");

            return;
        }

        Alert confirmation = new
                Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Annuler l'inscription");
        confirmation.setContentText("Êtes-vous sûr ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {

                    inscriptionController.annulerInscription(selected.getId());

                    chargerInscriptions();
                    viderFormulaire();
                    afficherMessage("Inscription annulée avec succès");

                } catch (Exception e) {
                    afficherErreur("Erreur: " + e.getMessage());
                }
            }
        });
    }
    @FXML
    private void enregistrerInscription() {
        try {
            if (txtMembreId.getText().isEmpty() ||

                    cmbEvenement.getValue() == null) {

                afficherErreur("Veuillez remplir tous les champs obligatoires");
                return;
            }
            int id = txtId.getText().isEmpty() ? 0 :

                    Integer.parseInt(txtId.getText());

            int membreId = Integer.parseInt(txtMembreId.getText());
            int evenementId = cmbEvenement.getValue().getId();
            if (id == 0) {
                inscriptionController.inscrireMembre(membreId, evenementId);

                afficherMessage("Inscription créée avec succès");
            } else {
                Inscription inscription = new Inscription(id,

                        membreId, evenementId);

                inscription.setStatut(cmbStatut.getValue());
                inscription.setConfirmed(chkConfirmed.isSelected());

                if (!txtMontant.getText().isEmpty()) {

                    inscription.setMontantPaye(Double.parseDouble(txtMontant.getText())
                    );

                }

                inscriptionController.modifierInscription(inscription);
                afficherMessage("Inscription modifiée avec succès");
            }
            chargerInscriptions();
            viderFormulaire();
        } catch (Exception e) {
            afficherErreur("Erreur: " + e.getMessage());
        }
    }
    @FXML
    private void validerPaiementInscription() {
        try {
            if (txtId.getText().isEmpty() ||

                    txtMontant.getText().isEmpty()) {

                afficherErreur("Veuillez sélectionner une inscription et saisir le montant");

                return;
            }
            int inscriptionId = Integer.parseInt(txtId.getText());
            double montant =

                    Double.parseDouble(txtMontant.getText());
            Inscription inscription =

                    inscriptionController.obtenirInscription(inscriptionId);

            if (inscription != null) {
                inscription.validerPaiement(montant);
                inscriptionController.modifierInscription(inscription);

                chargerInscriptions();
                afficherMessage("Paiement validé avec succès");
            }
        } catch (Exception e) {
            afficherErreur("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void viderFormulaire() {
        txtId.clear();
        txtMembreId.clear();
        cmbEvenement.setValue(null);
        lblDateInscription.setText("Non définie");
        cmbStatut.setValue(StatutInscription.EN_ATTENTE);
        txtMontant.clear();
        chkConfirmed.setSelected(false);
    }
    private void afficherInscriptionDansFormulaire(Inscription
                                                           inscription) {
        txtId.setText(String.valueOf(inscription.getId()));
        txtMembreId.setText(String.valueOf(inscription.getMembreId()));
        for (Evenement evt : cmbEvenement.getItems()) {
            if (evt.getId() == inscription.getEvenementId()) {
                cmbEvenement.setValue(evt);
                break;
            }
        }
        lblDateInscription.setText(
                inscription.getDateInscription().format(DateTimeFormatter.ofPattern
                        ("dd/MM/yyyy HH:mm"))
        );
        cmbStatut.setValue(inscription.getStatut());
        txtMontant.setText(String.valueOf(inscription.getMontantPaye()));
        chkConfirmed.setSelected(inscription.isConfirmed());
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
