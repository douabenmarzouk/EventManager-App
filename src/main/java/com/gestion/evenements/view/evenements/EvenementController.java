package com.gestion.evenements.view.evenements;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;  // ✅ AJOUTEZ CECI
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.net.URL;  // ✅ AJOUTEZ CECI
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;  // ✅ AJOUTEZ CECI

public class EvenementController implements Initializable {  // ✅ AJOUTEZ implements Initializable

    // Champs du formulaire
    @FXML private TextField txtNom;
    @FXML private DatePicker datePickerDate;
    @FXML private TextField txtLieu;
    @FXML private TextField txtCapacite;
    @FXML private TextArea txtDescription;

    // Boutons
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnSupprimer;
    @FXML private Button btnFermer;

    // Table et colonnes
    @FXML private TableView<Evenement> tableEvenements;
    @FXML private TableColumn<Evenement, Integer> colId;
    @FXML private TableColumn<Evenement, String> colNom;
    @FXML private TableColumn<Evenement, LocalDate> colDate;
    @FXML private TableColumn<Evenement, String> colLieu;
    @FXML private TableColumn<Evenement, Integer> colCapacite;
    @FXML private TableColumn<Evenement, String> colDescription;

    // Label de statut
    @FXML private Label lblStatus;

    // Liste des événements
    private ObservableList<Evenement> listeEvenements;
    private int prochainId = 1;

    @Override  // ✅ CHANGEZ LA SIGNATURE
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ EvenementController initialisé");

        // Initialiser la liste
        listeEvenements = FXCollections.observableArrayList();

        // Configurer les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Lier la table à la liste
        tableEvenements.setItems(listeEvenements);

        // Désactiver le bouton Modifier au démarrage
        if (btnModifier != null) {
            btnModifier.setDisable(true);
        }

        // Message de bienvenue
        afficherStatus("Prêt - Ajoutez votre premier événement");
    }
    @FXML
    public void ajouterEvenement(ActionEvent event) {
        if (!validerFormulaire()) {
            return;
        }
        try {
            Evenement nouvelEvenement = new Evenement(

                    prochainId++,
                    txtNom.getText().trim(),
                    datePickerDate.getValue(),
                    txtLieu.getText().trim(),
                    Integer.parseInt(txtCapacite.getText().trim()),
                    txtDescription.getText().trim()

            );

            listeEvenements.add(nouvelEvenement);
            reinitialiserFormulaire(null);
            afficherStatus("✓ Événement ajouté avec succès !");
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                    "L'événement \"" + nouvelEvenement.getNom() +

                            "\" a été ajouté.");
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "La capacité doit être un nombre entier valide.");
        }
    }
    @FXML
    public void modifierEvenement(ActionEvent event) {
        Evenement evenementSelectionne =
                tableEvenements.getSelectionModel().getSelectedItem();
        if (evenementSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un événement à modifier.");
            return;
        }
        if (!validerFormulaire()) {
            return;
        }
        try {
            evenementSelectionne.setNom(txtNom.getText().trim());

            evenementSelectionne.setDate(datePickerDate.getValue());

            evenementSelectionne.setLieu(txtLieu.getText().trim());
            evenementSelectionne.setCapacite(Integer.parseInt(txtCapacite.getText().trim()));
            evenementSelectionne.setDescription(txtDescription.getText().trim(
            ));

            tableEvenements.refresh();
            reinitialiserFormulaire(null);
            afficherStatus("✓ Événement modifié avec succès !");
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",

                    "L'événement a été modifié.");
        } catch (NumberFormatException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "La capacité doit être un nombre entier valide.");
        }
    }
    @FXML
    public void supprimerEvenement(ActionEvent event) {
        Evenement evenementSelectionne =
                tableEvenements.getSelectionModel().getSelectedItem();
        if (evenementSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner un événement à supprimer.");
            return;
        }
// Confirmation
        Optional<ButtonType> resultat = afficherConfirmation(

                "Confirmation de suppression",
                "Voulez-vous vraiment supprimer l'événement \"" +

                        evenementSelectionne.getNom() + "\" ?"
        );
        if (resultat.isPresent() && resultat.get() ==
                ButtonType.OK) {

            listeEvenements.remove(evenementSelectionne);
            reinitialiserFormulaire(null);
            afficherStatus("✓ Événement supprimé");
        }
    }
    @FXML
    public void selectionnerEvenement(MouseEvent event) {
        Evenement evenementSelectionne =
                tableEvenements.getSelectionModel().getSelectedItem();
        if (evenementSelectionne != null) {
// Remplir le formulaire avec les données sélectionnées
            txtNom.setText(evenementSelectionne.getNom());
            datePickerDate.setValue(evenementSelectionne.getDate());
            txtLieu.setText(evenementSelectionne.getLieu());

            txtCapacite.setText(String.valueOf(evenementSelectionne.getCapacite
                    ()));
            txtDescription.setText(evenementSelectionne.getDescription());

// Activer le bouton Modifier
            if (btnModifier != null) {
                btnModifier.setDisable(false);
            }
            afficherStatus("Événement sélectionné - Vous pouvez le modifier");
        }
    }
    @FXML
    public void reinitialiserFormulaire(ActionEvent event) {
        txtNom.clear();
        datePickerDate.setValue(null);
        txtLieu.clear();
        txtCapacite.clear();
        txtDescription.clear();
        tableEvenements.getSelectionModel().clearSelection();
        if (btnModifier != null) {
            btnModifier.setDisable(true);
        }
        afficherStatus("Formulaire réinitialisé");
    }
    @FXML
    public void actualiser(ActionEvent event) {
        tableEvenements.refresh();
        afficherStatus("✓ Liste actualisée");
    }
    @FXML
    public void fermer(ActionEvent event) {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();
        if (txtNom.getText().trim().isEmpty()) {
            erreurs.append("- Le nom de l'événement est obligatoire\n");

        }
        if (datePickerDate.getValue() == null) {
            erreurs.append("- La date est obligatoire\n");
        } else if
        (datePickerDate.getValue().isBefore(LocalDate.now())) {
            erreurs.append("- La date ne peut pas être dans le passé\n");
        }
        if (txtLieu.getText().trim().isEmpty()) {
            erreurs.append("- Le lieu est obligatoire\n");
        }
        if (txtCapacite.getText().trim().isEmpty()) {
            erreurs.append("- La capacité est obligatoire\n");
        } else {
            try {
                int capacite = Integer.parseInt(txtCapacite.getText().trim());

                if (capacite <= 0) {
                    erreurs.append("- La capacité doit être supérieure à 0\n");
                }
            } catch (NumberFormatException e) {
                erreurs.append("- La capacité doit être un nombre entier\n");
            }
        }
        if (erreurs.length() > 0) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreurs de validation", erreurs.toString());

            return false;
        }
        return true;
    }
    private void afficherStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }
    private void afficherAlerte(Alert.AlertType type, String titre,
                                String message) {
        Alert alert = new Alert(type);

        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private Optional<ButtonType> afficherConfirmation(String titre,
                                                      String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
    // Classe interne pour représenter un événement
    public static class Evenement {
        private int id;
        private String nom;
        private LocalDate date;
        private String lieu;
        private int capacite;
        private String description;
        public Evenement(int id, String nom, LocalDate date, String
                lieu, int capacite, String description) {

            this.id = id;
            this.nom = nom;
            this.date = date;
            this.lieu = lieu;
            this.capacite = capacite;
            this.description = description;
        }
        // Getters
        public int getId() { return id; }
        public String getNom() { return nom; }
        public LocalDate getDate() { return date; }
        public String getLieu() { return lieu; }
        public int getCapacite() { return capacite; }
        public String getDescription() { return description; }
        // Setters
        public void setId(int id) { this.id = id; }
        public void setNom(String nom) { this.nom = nom; }
        public void setDate(LocalDate date) { this.date = date; }
        public void setLieu(String lieu) { this.lieu = lieu; }

        public void setCapacite(int capacite) { this.capacite =
                capacite; }
        public void setDescription(String description) {
            this.description = description; }
    }
}
