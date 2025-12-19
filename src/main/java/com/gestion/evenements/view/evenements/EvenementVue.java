package com.gestion.evenements.view.evenements;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.gestion.evenements.controller.evenements.EvenementController;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.enums.StatutEvenement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class EvenementVue extends Application implements
        IEvenementVue {
    private EvenementController controller;
    private TableView<Evenement> tableEvenements;
    private ObservableList<Evenement> evenementsList;
    // Champs du formulaire
    private TextField txtId, txtTitre, txtLieu, txtCapacite,
            txtTarif;
    private TextArea txtDescription;
    private DatePicker dateDebut, dateFin;
    private ComboBox<StatutEvenement> cmbStatut;
    private TextField txtHeureDebut, txtHeureFin;
    @Override

    public void start(Stage primaryStage) {
        controller = new EvenementController();
        evenementsList = FXCollections.observableArrayList();
        primaryStage.setTitle("Gestion des Événements");
        BorderPane root = new BorderPane();
        root.setTop(creerBarreOutils());
        root.setCenter(creerTableEvenements());
        root.setRight(creerFormulaireEvenement());
        root.setBottom(creerBarreStatut());
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
        chargerEvenements();
    }
    private HBox creerBarreOutils() {
        HBox barre = new HBox(15);
        barre.setPadding(new Insets(15));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #34495e;");
        Button btnNouveau = new Button("➕ Nouveau");
        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("️ Supprimer");
        Button btnActualiser = new Button("🔄 Actualiser");
        Button btnRechercher = new Button("🔍 Rechercher");
        styliserBouton(btnNouveau, "#27ae60");
        styliserBouton(btnModifier, "#3498db");
        styliserBouton(btnSupprimer, "#e74c3c");
        styliserBouton(btnActualiser, "#95a5a6");
        styliserBouton(btnRechercher, "#f39c12");
        btnNouveau.setOnAction(e -> nouveauEvenement());
        btnModifier.setOnAction(e -> modifierEvenement());
        btnSupprimer.setOnAction(e -> supprimerEvenement());
        btnActualiser.setOnAction(e -> chargerEvenements());
        btnRechercher.setOnAction(e -> rechercherEvenement());
        barre.getChildren().addAll(

                btnNouveau, btnModifier, btnSupprimer,
                new Separator(), btnActualiser, btnRechercher

        );

        return barre;
    }
    private VBox creerTableEvenements() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        Label titre = new Label("📋 Liste des Événements");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        tableEvenements = new TableView<>();
        tableEvenements.setItems(evenementsList);
        TableColumn<Evenement, Integer> colId = new
                TableColumn<>("ID");
        colId.setCellValueFactory(new
                PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        TableColumn<Evenement, String> colTitre = new
                TableColumn<>("Titre");
        colTitre.setCellValueFactory(new
                PropertyValueFactory<>("titre"));
        colTitre.setPrefWidth(200);
        TableColumn<Evenement, String> colLieu = new
                TableColumn<>("Lieu");
        colLieu.setCellValueFactory(new
                PropertyValueFactory<>("lieu"));
        colLieu.setPrefWidth(150);
        TableColumn<Evenement, LocalDateTime> colDate = new
                TableColumn<>("Date Début");
        colDate.setCellValueFactory(new
                PropertyValueFactory<>("dateDebut"));
        colDate.setPrefWidth(150);
        colDate.setCellFactory(col -> new TableCell<Evenement,
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
        TableColumn<Evenement, Integer> colParticipants = new
                TableColumn<>("Participants");
        colParticipants.setCellValueFactory(new
                PropertyValueFactory<>("nbParticipants"));
        colParticipants.setPrefWidth(100);
        TableColumn<Evenement, Integer> colCapacite = new
                TableColumn<>("Capacité");
        colCapacite.setCellValueFactory(new
                PropertyValueFactory<>("capaciteMax"));
        colCapacite.setPrefWidth(80);
        TableColumn<Evenement, Double> colTarif = new
                TableColumn<>("Tarif (DT)");
        colTarif.setCellValueFactory(new
                PropertyValueFactory<>("tarif"));
        colTarif.setPrefWidth(100);
        TableColumn<Evenement, StatutEvenement> colStatut = new
                TableColumn<>("Statut");
        colStatut.setCellValueFactory(new
                PropertyValueFactory<>("statut"));
        colStatut.setPrefWidth(120);
        tableEvenements.getColumns().addAll(
                colId, colTitre, colLieu, colDate,
                colParticipants, colCapacite, colTarif, colStatut

        );

        tableEvenements.getSelectionModel().selectedItemProperty().addListener(

                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherEvenementDansFormulaire(newSelection);

                    }
                }
        );
        container.getChildren().addAll(titre, tableEvenements);

        VBox.setVgrow(tableEvenements, Priority.ALWAYS);
        return container;
    }
    private ScrollPane creerFormulaireEvenement() {
        VBox formulaire = new VBox(15);
        formulaire.setPadding(new Insets(15));
        formulaire.setPrefWidth(350);
        formulaire.setStyle("-fx-background-color: #ecf0f1;");
        Label titre = new Label("📝 Détails de l'Événement");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
// ID
        txtId = new TextField();
        txtId.setEditable(false);
        txtId.setPromptText("Auto-généré");
// Titre
        txtTitre = new TextField();
        txtTitre.setPromptText("Titre de l'événement");
// Description
        txtDescription = new TextArea();
        txtDescription.setPromptText("Description de l'événement");
        txtDescription.setPrefRowCount(3);
        txtDescription.setWrapText(true);
// Lieu
        txtLieu = new TextField();
        txtLieu.setPromptText("Lieu de l'événement");
// Date et heure début
        dateDebut = new DatePicker();
        dateDebut.setPromptText("Date de début");
        txtHeureDebut = new TextField("09:00");
        txtHeureDebut.setPromptText("HH:mm");
        txtHeureDebut.setPrefWidth(80);
        HBox hboxDebut = new HBox(10, dateDebut, txtHeureDebut);
// Date et heure fin
        dateFin = new DatePicker();
        dateFin.setPromptText("Date de fin");
        txtHeureFin = new TextField("18:00");
        txtHeureFin.setPromptText("HH:mm");

        txtHeureFin.setPrefWidth(80);
        HBox hboxFin = new HBox(10, dateFin, txtHeureFin);
// Capacité
        txtCapacite = new TextField();
        txtCapacite.setPromptText("Capacité maximale");
// Tarif
        txtTarif = new TextField();
        txtTarif.setPromptText("Tarif (DT)");
// Statut
        cmbStatut = new ComboBox<>();
        cmbStatut.setItems(FXCollections.observableArrayList(StatutEvenement.values()));
        cmbStatut.setValue(StatutEvenement.PLANIFIE);
        cmbStatut.setMaxWidth(Double.MAX_VALUE);
// Boutons d'action
        Button btnEnregistrer = new Button("💾 Enregistrer");
        Button btnAnnuler = new Button("❌ Annuler");
        btnEnregistrer.setMaxWidth(Double.MAX_VALUE);
        btnAnnuler.setMaxWidth(Double.MAX_VALUE);
        styliserBouton(btnEnregistrer, "#27ae60");
        styliserBouton(btnAnnuler, "#e74c3c");
        btnEnregistrer.setOnAction(e -> enregistrerEvenement());
        btnAnnuler.setOnAction(e -> viderFormulaire());
        formulaire.getChildren().addAll(

                titre,
                new Label("ID:"), txtId,
                new Label("Titre:"), txtTitre,
                new Label("Description:"), txtDescription,
                new Label("Lieu:"), txtLieu,
                new Label("Date & Heure Début:"), hboxDebut,
                new Label("Date & Heure Fin:"), hboxFin,
                new Label("Capacité Maximale:"), txtCapacite,
                new Label("Tarif (DT):"), txtTarif,
                new Label("Statut:"), cmbStatut,
                new Separator(),
                btnEnregistrer,
                btnAnnuler

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
    private void chargerEvenements() {
        List<Evenement> evenements = controller.listerEvenements();
        evenementsList.clear();
        evenementsList.addAll(evenements);
    }
    private void nouveauEvenement() {
        viderFormulaire();
        txtTitre.requestFocus();
    }
    private void modifierEvenement() {
        Evenement selected =
                tableEvenements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Veuillez sélectionner un événement à modifier");
            return;
        }

        afficherEvenementDansFormulaire(selected);
    }
    private void supprimerEvenement() {
        Evenement selected =
                tableEvenements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Veuillez sélectionner un événement à supprimer");
            return;
        }
        Alert confirmation = new
                Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer l'événement");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'événement '" +

        selected.getTitre() + "' ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {

                    controller.supprimerEvenement(selected.getId());

                    chargerEvenements();
                    viderFormulaire();
                    afficherMessage("Événement supprimé avec succès");

                } catch (Exception ex) {
                    afficherErreur("Erreur: " + ex.getMessage());
                }
            }
        });
    }
    private void rechercherEvenement() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rechercher un événement");
        dialog.setHeaderText("Recherche d'événement");
        dialog.setContentText("Entrez le titre ou une partie du titre:");
        dialog.showAndWait().ifPresent(critere -> {
            List<Evenement> resultats =
                    controller.rechercherEvenement(critere);
            evenementsList.clear();
            evenementsList.addAll(resultats);

        });
    }
    private void enregistrerEvenement() {
        try {
// Validation des champs
            if (txtTitre.getText().isEmpty() ||

                    txtLieu.getText().isEmpty() ||

                    dateDebut.getValue() == null ||

                    dateFin.getValue() == null ||

                    txtCapacite.getText().isEmpty() ||

                    txtTarif.getText().isEmpty()) {

                afficherErreur("Veuillez remplir tous les champs obligatoires");
                return;
            }
// Conversion des dates
            LocalDateTime debut = LocalDateTime.of(

                    dateDebut.getValue(),

                    java.time.LocalTime.parse(txtHeureDebut.getText())

            );
            LocalDateTime fin = LocalDateTime.of(

                    dateFin.getValue(),

                    java.time.LocalTime.parse(txtHeureFin.getText())

            );
            int id = txtId.getText().isEmpty() ? 0 :

                    Integer.parseInt(txtId.getText());

            Evenement evenement = new Evenement(

                    id,
                    txtTitre.getText(),
                    txtDescription.getText(),
                    debut,
                    fin,
                    txtLieu.getText(),
                    Integer.parseInt(txtCapacite.getText()),
                    Double.parseDouble(txtTarif.getText())

            );
            evenement.setStatut(cmbStatut.getValue());
            if (id == 0) {
                controller.creerEvenement(evenement);
                afficherMessage("Événement créé avec succès");

            } else {
                controller.modifierEvenement(evenement);
                afficherMessage("Événement modifié avec succès");
            }
            chargerEvenements();
            viderFormulaire();
        } catch (Exception e) {
            afficherErreur("Erreur lors de l'enregistrement: " +

                    e.getMessage());
        }
    }
    private void afficherEvenementDansFormulaire(Evenement
                                                         evenement) {
        txtId.setText(String.valueOf(evenement.getId()));
        txtTitre.setText(evenement.getTitre());
        txtDescription.setText(evenement.getDescription());
        txtLieu.setText(evenement.getLieu());
        dateDebut.setValue(evenement.getDateDebut().toLocalDate());
        txtHeureDebut.setText(evenement.getDateDebut().toLocalTime().toString());
        dateFin.setValue(evenement.getDateFin().toLocalDate());
        txtHeureFin.setText(evenement.getDateFin().toLocalTime().toString(
        ));
        txtCapacite.setText(String.valueOf(evenement.getCapaciteMax()));
        txtTarif.setText(String.valueOf(evenement.getTarif()));
        cmbStatut.setValue(evenement.getStatut());
    }
    private void viderFormulaire() {
        txtId.clear();
        txtTitre.clear();
        txtDescription.clear();
        txtLieu.clear();
        dateDebut.setValue(null);
        dateFin.setValue(null);
        txtHeureDebut.setText("09:00");
        txtHeureFin.setText("18:00");
        txtCapacite.clear();
        txtTarif.clear();
        cmbStatut.setValue(StatutEvenement.PLANIFIE);
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
    }
}
