package com.gestion.evenements.view.evenements;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.gestion.evenements.controller.evenements.EvenementController;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.enums.StatutEvenement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EvenementVueController {
    @FXML private TableView<Evenement> tableEvenements;
    @FXML private TableColumn<Evenement, Integer> colId;
    @FXML private TableColumn<Evenement, String> colTitre;
    @FXML private TableColumn<Evenement, String> colLieu;
    @FXML private TableColumn<Evenement, LocalDateTime>
            colDateDebut;
    @FXML private TableColumn<Evenement, Integer> colParticipants;
    @FXML private TableColumn<Evenement, Integer> colCapacite;

    @FXML private TableColumn<Evenement, Double> colTarif;
    @FXML private TableColumn<Evenement, StatutEvenement>
            colStatut;
    @FXML private TextField txtId;
    @FXML private TextField txtTitre;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtLieu;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private TextField txtHeureDebut;
    @FXML private TextField txtHeureFin;
    @FXML private TextField txtCapacite;
    @FXML private TextField txtTarif;
    @FXML private ComboBox<StatutEvenement> cmbStatut;
    private EvenementController controller;
    private ObservableList<Evenement> evenementsList;
    @FXML
    public void initialize() {
        controller = new EvenementController();
        evenementsList = FXCollections.observableArrayList();
// Configuration des colonnes
        colId.setCellValueFactory(new
                PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new
                PropertyValueFactory<>("titre"));
        colLieu.setCellValueFactory(new
                PropertyValueFactory<>("lieu"));
        colDateDebut.setCellValueFactory(new
                PropertyValueFactory<>("dateDebut"));
        colParticipants.setCellValueFactory(new
                PropertyValueFactory<>("nbParticipants"));
        colCapacite.setCellValueFactory(new
                PropertyValueFactory<>("capaciteMax"));
        colTarif.setCellValueFactory(new
                PropertyValueFactory<>("tarif"));
        colStatut.setCellValueFactory(new
                PropertyValueFactory<>("statut"));
// Format date
        colDateDebut.setCellFactory(col -> new TableCell<Evenement,
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
        tableEvenements.setItems(evenementsList);
// Listener pour afficher les détails
        tableEvenements.getSelectionModel().selectedItemProperty().addListener(

                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        afficherEvenementDansFormulaire(newSelection);

                    }
                }
        );
// Initialiser le ComboBox
        cmbStatut.setItems(FXCollections.observableArrayList(StatutEvenement.values()));
        cmbStatut.setValue(StatutEvenement.PLANIFIE);
// Charger les données
        chargerEvenements();
    }
    @FXML
    private void chargerEvenements() {
        List<Evenement> evenements = controller.listerEvenements();
        evenementsList.clear();
        evenementsList.addAll(evenements);
    }
    @FXML
    private void nouveauEvenement() {
        viderFormulaire();
        txtTitre.requestFocus();
    }

    @FXML
    private void modifierEvenement() {
        Evenement selected =
                tableEvenements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherErreur("Veuillez sélectionner un événement à modifier");
            return;
        }
        afficherEvenementDansFormulaire(selected);
    }
    @FXML
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
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selected.getTitre() + "' ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {

                    controller.supprimerEvenement(selected.getId());

                    chargerEvenements();
                    viderFormulaire();
                    afficherMessage("Événement supprimé avec succès");

                } catch (Exception e) {
                    afficherErreur("Erreur: " + e.getMessage());
                }
            }
        });
    }
    @FXML
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
    @FXML
    private void enregistrerEvenement() {
        try {
            if (txtTitre.getText().isEmpty() ||

                    txtLieu.getText().isEmpty() ||

                    dateDebut.getValue() == null ||

                    dateFin.getValue() == null ||

                    txtCapacite.getText().isEmpty() ||

                    txtTarif.getText().isEmpty()) {

                afficherErreur("Veuillez remplir tous les champs obligatoires");
                return;
            }
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
            afficherErreur("Erreur: " + e.getMessage());
        }
    }
    @FXML
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
    private void afficherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void afficherErreur(String erreur) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(erreur);
        alert.showAndWait();
    }
}
