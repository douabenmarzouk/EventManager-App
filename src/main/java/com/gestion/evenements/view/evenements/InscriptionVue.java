package com.gestion.evenements.view.evenements;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import com.gestion.evenements.controller.evenements.InscriptionController;
import com.gestion.evenements.controller.evenements.EvenementController;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.gestion.evenements.controller.evenements.InscriptionController;
import com.gestion.evenements.controller.evenements.EvenementController;
import com.gestion.evenements.model.evenements.entities.Inscription;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.enums.StatutInscription;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class InscriptionVue extends Application implements
        IEvenementVue {
    private InscriptionController inscriptionController;
    private EvenementController evenementController;
    private TableView<Inscription> tableInscriptions;
    private ObservableList<Inscription> inscriptionsList;
    private TextField txtId, txtMembreId, txtMontant;
    private ComboBox<Evenement> cmbEvenement;
    private ComboBox<StatutInscription> cmbStatut;
    private Label lblDateInscription;
    private CheckBox chkConfirmed;
    @Override
    public void start(Stage primaryStage) {
        inscriptionController = new InscriptionController();
        evenementController = new EvenementController();
        inscriptionsList = FXCollections.observableArrayList();
        primaryStage.setTitle("Gestion des Inscriptions");
        BorderPane root = new BorderPane();
        root.setTop(creerBarreOutils());
        root.setCenter(creerTableInscriptions());
        root.setRight(creerFormulaireInscription());
        root.setBottom(creerBarreStatut());
        Scene scene = new Scene(root, 1200, 700);

        primaryStage.setScene(scene);
        primaryStage.show();
        chargerInscriptions();
        chargerEvenements();
    }
    private HBox creerBarreOutils() {
        HBox barre = new HBox(15);
        barre.setPadding(new Insets(15));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #16a085;");
        Button btnNouveau = new Button("➕ Nouvelle Inscription");
        Button btnValider = new Button("✅ Valider");
        Button btnConfirmer = new Button("✔️ Confirmer");
        Button btnAnnuler = new Button("❌ Annuler");
        Button btnActualiser = new Button("🔄 Actualiser");
        styliserBouton(btnNouveau, "#27ae60");
        styliserBouton(btnValider, "#2ecc71");
        styliserBouton(btnConfirmer, "#3498db");
        styliserBouton(btnAnnuler, "#e74c3c");
        styliserBouton(btnActualiser, "#95a5a6");
        btnNouveau.setOnAction(e -> nouvelleInscription());
        btnValider.setOnAction(e -> validerInscription());
        btnConfirmer.setOnAction(e -> confirmerInscription());
        btnAnnuler.setOnAction(e -> annulerInscription());
        btnActualiser.setOnAction(e -> chargerInscriptions());
        barre.getChildren().addAll(

                btnNouveau,
                new Separator(),
                btnValider, btnConfirmer, btnAnnuler,
                new Separator(),
                btnActualiser

        );
        return barre;
    }
    private VBox creerTableInscriptions() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        Label titre = new Label("📝 Liste des Inscriptions");

        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        tableInscriptions = new TableView<>();
        tableInscriptions.setItems(inscriptionsList);
        TableColumn<Inscription, Integer> colId = new
                TableColumn<>("ID");
        colId.setCellValueFactory(new
                PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        TableColumn<Inscription, Integer> colMembreId = new
                TableColumn<>("Membre ID");
        colMembreId.setCellValueFactory(new
                PropertyValueFactory<>("membreId"));
        colMembreId.setPrefWidth(100);
        TableColumn<Inscription, Integer> colEvenementId = new
                TableColumn<>("Événement ID");
        colEvenementId.setCellValueFactory(new
                PropertyValueFactory<>("evenementId"));
        colEvenementId.setPrefWidth(120);
        TableColumn<Inscription, LocalDateTime> colDate = new
                TableColumn<>("Date Inscription");
        colDate.setCellValueFactory(new
                PropertyValueFactory<>("dateInscription"));
        colDate.setPrefWidth(180);
        colDate.setCellFactory(col -> new TableCell<Inscription,
                LocalDateTime>() {
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
        TableColumn<Inscription, StatutInscription> colStatut = new
                TableColumn<>("Statut");

        colStatut.setCellValueFactory(new
                PropertyValueFactory<>("statut"));
        colStatut.setPrefWidth(120);
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
        TableColumn<Inscription, Double> colMontant = new
                TableColumn<>("Montant Payé");
        colMontant.setCellValueFactory(new
                PropertyValueFactory<>("montantPaye"));
        colMontant.setPrefWidth(120);
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
        TableColumn<Inscription, Boolean> colConfirmed = new
                TableColumn<>("Confirmé");
        colConfirmed.setCellValueFactory(new
                PropertyValueFactory<>("confirmed"));
        colConfirmed.setPrefWidth(100);
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
        tableInscriptions.getColumns().addAll(

                colId, colMembreId, colEvenementId, colDate,
                colStatut, colMontant, colConfirmed

        );

        tableInscriptions.getSelectionModel().selectedItemProperty().addListener(

                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherInscriptionDansFormulaire(newSelection);

                    }
                }
        );
        container.getChildren().addAll(titre, tableInscriptions);
        VBox.setVgrow(tableInscriptions, Priority.ALWAYS);
        return container;
    }

    private ScrollPane creerFormulaireInscription() {
        VBox formulaire = new VBox(15);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(350);
        formulaire.setStyle("-fx-background-color: #ecf0f1;");
        Label titre = new Label("✍️ Détails de l'Inscription");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
// ID
        txtId = new TextField();
        txtId.setEditable(false);
        txtId.setPromptText("Auto-généré");
// Membre ID
        txtMembreId = new TextField();
        txtMembreId.setPromptText("ID du membre");
// Événement
        cmbEvenement = new ComboBox<>();
        cmbEvenement.setMaxWidth(Double.MAX_VALUE);
        cmbEvenement.setPromptText("Sélectionnez un événement");
// Date d'inscription
        lblDateInscription = new Label("Non définie");
        lblDateInscription.setStyle("-fx-font-style: italic;");
// Statut
        cmbStatut = new ComboBox<>();
        cmbStatut.setItems(FXCollections.observableArrayList(StatutInscription.values()));
        cmbStatut.setValue(StatutInscription.EN_ATTENTE);
        cmbStatut.setMaxWidth(Double.MAX_VALUE);
// Montant
        txtMontant = new TextField();
        txtMontant.setPromptText("Montant payé (DT)");
// Confirmé
        chkConfirmed = new CheckBox("Inscription confirmée");
// Boutons d'action
        Button btnEnregistrer = new Button("💾 Enregistrer");
        Button btnValiderPaiement = new Button("💰 Valider Paiement");
                Button btnReinitialiser = new Button("🔄 Réinitialiser");

        btnEnregistrer.setMaxWidth(Double.MAX_VALUE);
        btnValiderPaiement.setMaxWidth(Double.MAX_VALUE);
        btnReinitialiser.setMaxWidth(Double.MAX_VALUE);
        styliserBouton(btnEnregistrer, "#27ae60");
        styliserBouton(btnValiderPaiement, "#3498db");
        styliserBouton(btnReinitialiser, "#95a5a6");
        btnEnregistrer.setOnAction(e -> enregistrerInscription());
        btnValiderPaiement.setOnAction(e ->
                validerPaiementInscription());
        btnReinitialiser.setOnAction(e -> viderFormulaire());
        formulaire.getChildren().addAll(

                titre,
                new Label("ID:"), txtId,
                new Label("ID Membre:"), txtMembreId,
                new Label("Événement:"), cmbEvenement,
                new Label("Date d'inscription:"),

                lblDateInscription,

                new Label("Statut:"), cmbStatut,
                new Label("Montant Payé:"), txtMontant,
                chkConfirmed,
                new Separator(),
                btnEnregistrer,
                btnValiderPaiement,
                btnReinitialiser

        );
        ScrollPane scrollPane = new ScrollPane(formulaire);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
    private HBox creerBarreStatut() {
        HBox barre = new HBox(10);
        barre.setPadding(new Insets(10));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #2c3e50;");
        Label lblStatut = new Label("Prêt");
        lblStatut.setStyle("-fx-text-fill: white;");
        barre.getChildren().add(lblStatut);
        return barre;
    }

    private void styliserBouton(Button btn, String couleur) {
        btn.setStyle(

                "-fx-background-color: " + couleur + ";" +

                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15;" +
                        "-fx-background-radius: 5;"

        );
    }
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
    private void nouvelleInscription() {
        viderFormulaire();
        txtMembreId.requestFocus();
    }
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
            afficherErreur("Erreur lors de la validation: " +

                    e.getMessage());
        }

    }
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
            afficherErreur("Erreur lors de la confirmation: " +

                    e.getMessage());
        }
    }
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
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler cette inscription ?");
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try {

                            inscriptionController.annulerInscription(selected.getId());

                            chargerInscriptions();
                            viderFormulaire();
                            afficherMessage("Inscription annulée avec succès");

                        } catch (Exception e) {

                            afficherErreur("Erreur lors de l'annulation: "

                                    + e.getMessage());
                        }
                    }
                });
    }
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
                inscriptionController.inscrireMembre(membreId,

                        evenementId);

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

            afficherErreur("Erreur lors de l'enregistrement: " +

                    e.getMessage());
        }
    }
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
            afficherErreur("Erreur lors de la validation du paiement: " + e.getMessage());
        }
    }
    private void afficherInscriptionDansFormulaire(Inscription
                                                           inscription) {
        txtId.setText(String.valueOf(inscription.getId()));
        txtMembreId.setText(String.valueOf(inscription.getMembreId()));
// Rechercher l'événement correspondant
        for (Evenement evt : cmbEvenement.getItems()) {
            if (evt.getId() == inscription.getEvenementId()) {
                cmbEvenement.setValue(evt);
                break;
            }
        }

        lblDateInscription.setText(

                inscription.getDateInscription().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

)
);
        cmbStatut.setValue(inscription.getStatut());
        txtMontant.setText(String.valueOf(inscription.getMontantPaye()));
        chkConfirmed.setSelected(inscription.isConfirmed());
    }
    private void viderFormulaire() {
        txtId.clear();
        txtMembreId.clear();
        cmbEvenement.setValue(null);
        lblDateInscription.setText("Non définie");
        cmbStatut.setValue(StatutInscription.EN_ATTENTE);
        txtMontant.clear();
        chkConfirmed.setSelected(false);
    }
    @Override
    public void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Override
    public void afficherErreur(String erreur) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(erreur);
        alert.showAndWait();
    }}