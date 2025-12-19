package com.gestion.evenements.controller;

import com.gestion.evenements.database.dao.AssociationDAO;
import com.gestion.evenements.database.dao.FeedbackDAO;
import com.gestion.evenements.database.dao.MembreDAO;
import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.exceptions.MembreException;
import com.gestion.evenements.model.membres.record.Feedback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MembreEspaceController {

    // Status Cards
    @FXML private VBox cotisationStatusCard;
    @FXML private Label cotisationStatusLabel;
    @FXML private Label cotisationMontantLabel;
    @FXML private Label cotisationDateLabel;
    @FXML private Label membreStatutLabel;
    @FXML private Label membreNumeroLabel;

    @FXML private Label welcomeMessageMembre;

    // Payer Cotisation
    @FXML private TextField montantCotisationField;
    @FXML private Button payerCotisationBtn;

    // Ajouter Feedback
    @FXML private Spinner<Integer> noteSpinner;
    @FXML private TextArea commentaireFeedbackArea;

    // Modifier Compte
    @FXML private TextField nomMembreField;
    @FXML private TextField prenomMembreField;
    @FXML private TextField emailMembreField;
    @FXML private TextField telephoneMembreField;

    // Événements
    @FXML private ListView<String> evenementsListView;

    // Table Feedbacks
    @FXML private TableView<FeedbackDisplay> mesFeedbacksTable;
    @FXML private TableColumn<FeedbackDisplay, String> feedbackDateCol;
    @FXML private TableColumn<FeedbackDisplay, Integer> feedbackNoteCol;
    @FXML private TableColumn<FeedbackDisplay, String> feedbackCommentCol;


    // Données
    private Membre currentMembre;
    private Association currentAssociation; // ✅ Référence à l'association
    private int associationId = 1; // ID de l'association (à passer depuis le login)

    // ✅ DAOs
    private MembreDAO membreDAO;
    private FeedbackDAO feedbackDAO;
    private AssociationDAO associationDAO;

    private ObservableList<FeedbackDisplay> feedbacksList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ✅ Initialiser les DAOs
        membreDAO = new MembreDAO();
        feedbackDAO = new FeedbackDAO();
        associationDAO = new AssociationDAO();

        // Initialiser le Spinner de notes (1-5)
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5);
        noteSpinner.setValueFactory(valueFactory);

        // Configurer les colonnes du tableau feedbacks
        feedbackDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        feedbackNoteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        feedbackCommentCol.setCellValueFactory(new PropertyValueFactory<>("commentaire"));

        mesFeedbacksTable.setItems(feedbacksList);

        // Charger des événements exemples
        ObservableList<String> evenements = FXCollections.observableArrayList(
                "📅 Conférence Tech 2025 - 15 Jan",
                "🎓 Workshop JavaFX - 20 Jan",
                "🎉 Soirée Networking - 25 Jan"
        );
        evenementsListView.setItems(evenements);

        // ✅ Charger l'association
        chargerAssociation();

        // Charger un membre par défaut pour tester
        loadMembreParDefaut();
    }

    // ✅ CHARGER L'ASSOCIATION DEPUIS LA BD
    private void chargerAssociation() {
        try {
            currentAssociation = associationDAO.getParId(associationId);
            if (currentAssociation == null) {
                // Créer une association par défaut si elle n'existe pas
                currentAssociation = new Association();
                currentAssociation.setId(associationId);
                currentAssociation.setNom("Association Tech Students");
                System.out.println("⚠️ Association non trouvée en BD, utilisation d'une association par défaut");
            } else {
                System.out.println("✅ Association chargée : " + currentAssociation.getNom());
                System.out.println("   Solde actuel : " + currentAssociation.getSolde() + " €");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur de chargement de l'association");
        }
    }

    private void loadMembreParDefaut() {
        try {
            // TODO: Charger depuis la BD via le login
            currentMembre = new Membre(1, "Dupont", "Jean", "jean.dupont@email.com", "0612345678");
            loadMembreInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMembre(Membre membre) {
        this.currentMembre = membre;
        loadMembreInfo();
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
        chargerAssociation();
    }

    private void loadMembreInfo() {
        if (currentMembre == null) return;

        welcomeMessageMembre.setText("Bienvenue " + currentMembre.getNomComplet() + " !");
        updateCotisationStatus();

        membreStatutLabel.setText(currentMembre.getStatut().getLibelle());
        membreStatutLabel.setStyle(
                currentMembre.getStatut().name().equals("ACTIF")
                        ? "-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 18px;"
                        : "-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 18px;"
        );

        membreNumeroLabel.setText("N° " + currentMembre.getNumeroMembre());


        nomMembreField.setText(currentMembre.getNom());
        prenomMembreField.setText(currentMembre.getPrenom());
        emailMembreField.setText(currentMembre.getEmail());
        telephoneMembreField.setText(currentMembre.getTelephone());
        montantCotisationField.setText(String.valueOf(currentMembre.getMontantCotisation()));

        loadFeedbacks();
    }

    private void updateCotisationStatus() {
        if (currentMembre.isCotisationPayee()) {
            cotisationStatusLabel.setText("✅ Cotisation Payée");
            cotisationStatusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 18px;");
            cotisationStatusCard.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 12; " +
                            "-fx-border-color: #10b981; -fx-border-radius: 12; -fx-border-width: 2; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(16,185,129,0.2), 8, 0, 0, 2); " +
                            "-fx-padding: 30 50;"
            );

            if (currentMembre.getDatePaiementCotisation() != null) {
                cotisationDateLabel.setText("Payée le " +
                        currentMembre.getDatePaiementCotisation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            payerCotisationBtn.setDisable(true);
            payerCotisationBtn.setText("✅ Déjà Payée");

        } else {
            cotisationStatusLabel.setText("❌ Cotisation Non Payée");
            cotisationStatusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 18px;");
            cotisationStatusCard.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 12; " +
                            "-fx-border-color: #ef4444; -fx-border-radius: 12; -fx-border-width: 2; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(239,68,68,0.2), 8, 0, 0, 2); " +
                            "-fx-padding: 30 50;"
            );
            cotisationDateLabel.setText("");
            payerCotisationBtn.setDisable(false);
            payerCotisationBtn.setText("💳 Payer Maintenant");
        }

        cotisationMontantLabel.setText("Montant: " + currentMembre.getMontantCotisation() + " €");
    }

    private void loadFeedbacks() {
        feedbacksList.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Feedback fb : currentMembre.getFeedbacks()) {
            feedbacksList.add(new FeedbackDisplay(
                    fb.date().format(formatter),
                    fb.note(),
                    fb.commentaire()
            ));
        }
        mesFeedbacksTable.setItems(feedbacksList);
    }

    // =================== ACTIONS ===================

    @FXML
    public void onPayerCotisation() {
        if (currentMembre == null) {
            showError("Aucun membre connecté !");
            return;
        }

        try {
            double montant = Double.parseDouble(montantCotisationField.getText().trim());
            if (montant <= 0) {
                showError("Montant invalide !");
                return;
            }

            // Confirmation simple
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Paiement");
            confirm.setContentText("Payer " + montant + " € ?");
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

            AssociationDAO dao = new AssociationDAO();

            // CETTE LIGNE FAIT TOUT EN UNE FOIS :
            // → Marque le membre comme payé
            // → Ajoute le montant au solde de l'association
            // → Tout est sauvegardé en base en une seule transaction
            dao.enregistrerPaiementCotisation(currentAssociation.getId(), currentMembre.getId(), montant);

            // Recharger TOUT depuis la base (le plus sûr)
            Association refreshed = dao.findByIdAvecMembres(currentAssociation.getId());

            // Mettre à jour les objets en mémoire
            currentAssociation.getMembres().setAll(refreshed.getMembres());
            currentAssociation.setSolde(refreshed.getSolde());

            // Mettre à jour le membre connecté
            currentMembre = currentAssociation.getMembres().stream()
                    .filter(m -> m.getId() == currentMembre.getId())
                    .findFirst()
                    .orElse(currentMembre);

            // Rafraîchir l'interface membre
            updateCotisationStatus();

            // Rafraîchir le dashboard admin EN DIRECT (même s’il est ouvert)
            AssociationController.refreshFeedbacksIfOpen();

            showSuccess("Paiement réussi !\n" + montant + " € ajoutés au solde de l’association");

        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onAjouterFeedback() {
        if (currentMembre == null) {
            showError("Connectez-vous d'abord !");
            return;
        }

        String commentaire = commentaireFeedbackArea.getText().trim();
        if (commentaire.isEmpty()) {
            showError("Écrivez un commentaire !");
            return;
        }

        int note = noteSpinner.getValue();

        try {
            Feedback feedback = new Feedback(
                    currentMembre.getId(),
                    note,
                    commentaire,
                    LocalDate.now()
            );

            // Sauvegarde en base
            new FeedbackDAO().creer(feedback, currentAssociation.getId());

            // Ajouter au membre en mémoire (sans risque d’exception)
            currentMembre.getFeedbacks().add(feedback);

            // Ajouter dans la petite table du membre
            feedbacksList.add(new FeedbackDisplay(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    note,
                    commentaire
            ));

            // Vider le champ
            commentaireFeedbackArea.clear();
            noteSpinner.getValueFactory().setValue(5);

            // Rafraîchir le tableau de bord admin EN DIRECT
            AssociationController.refreshFeedbacksIfOpen();

            showSuccess("Merci ! Votre avis a été envoyé");

        } catch (SQLException e) {
            showError("Erreur envoi feedback : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void onModifierCompte() {
        if (currentMembre == null) {
            showError("Aucun membre connecté !");
            return;
        }

        String nom = nomMembreField.getText().trim();
        String prenom = prenomMembreField.getText().trim();
        String email = emailMembreField.getText().trim();
        String telephone = telephoneMembreField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires !");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer les modifications");
        confirm.setHeaderText("Modifier les informations");
        confirm.setContentText("Voulez-vous enregistrer ces modifications ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // ✅ Mettre à jour le membre
                    currentMembre.setNom(nom);
                    currentMembre.setPrenom(prenom);
                    currentMembre.setEmail(email);
                    currentMembre.setTelephone(telephone);

                    // ✅ Sauvegarder en BD
                    membreDAO.mettreAJour(currentMembre);

                    welcomeMessageMembre.setText("Bienvenue " + currentMembre.getNomComplet() + " !");

                    // ✅ Rafraîchir l'AssociationController si ouvert
                    AssociationController.refreshFeedbacksIfOpen();

                    showSuccess("✅ Informations mises à jour avec succès !");

                } catch (SQLException e) {
                    showError("Erreur de sauvegarde : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void onVoirEvenements() {
        showInfo("Événements",
                "Redirection vers la page des événements...\n" +
                        "Vous pourrez consulter tous les événements disponibles et vous inscrire."
        );
    }

    @FXML
    public void onLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        confirm.setContentText("Vous devrez vous reconnecter pour accéder à l'application.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
                    javafx.scene.Parent root = loader.load();

                    javafx.stage.Stage stage = (javafx.stage.Stage) mesFeedbacksTable.getScene().getWindow();
                    javafx.scene.Scene scene = new javafx.scene.Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Gestion d'Événements - Connexion");
                    stage.setWidth(1000);
                    stage.setHeight(700);
                    stage.centerOnScreen();
                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Erreur lors de la déconnexion !");
                }
            }
        });
    }


    // =================== UTILITAIRES ===================

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe pour l'affichage des feedbacks
    public static class FeedbackDisplay {
        private final String date;
        private final int note;
        private final String commentaire;

        public FeedbackDisplay(String date, int note, String commentaire) {
            this.date = date;
            this.note = note;
            this.commentaire = commentaire;
        }

        public String getDate() { return date; }
        public int getNote() { return note; }
        public String getCommentaire() { return commentaire; }
    }
}