package com.gestion.evenements.view.evenements;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;  // ✅ AJOUTEZ
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;  // ✅ AJOUTEZ
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;  // ✅ AJOUTEZ
import java.util.regex.Pattern;

public class InscriptionController implements Initializable {  // ✅ AJOUTEZ implements Initializable

    // Formulaire
    @FXML private ComboBox<String> comboEvenement;
    @FXML private TextField txtNomParticipant;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private ComboBox<String> comboStatut;
    @FXML private TextArea txtNotes;

    // Boutons
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnFermer;

    // Table
    @FXML private TableView<Inscription> tableInscriptions;
    @FXML private TableColumn<Inscription, Integer> colId;
    @FXML private TableColumn<Inscription, String> colParticipant;
    @FXML private TableColumn<Inscription, String> colEmail;
    @FXML private TableColumn<Inscription, String> colTelephone;
    @FXML private TableColumn<Inscription, String> colDateInscription;
    @FXML private TableColumn<Inscription, String> colStatut;

    // Filtre et labels
    @FXML private ComboBox<String> comboFiltreEvenement;
    @FXML private Label lblStatus;
    @FXML private Label lblNombreInscriptions;

    // Données
    private ObservableList<Inscription> listeInscriptions;
    private ObservableList<Inscription> listeInscriptionsComplete;
    private int prochainId = 1;

    // Pattern pour validation email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override  // ✅ AJOUTEZ @Override et changez la signature
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ InscriptionController initialisé");

        // Initialiser les listes
        listeInscriptionsComplete = FXCollections.observableArrayList();
        listeInscriptions = FXCollections.observableArrayList();

        // Configurer les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colParticipant.setCellValueFactory(new PropertyValueFactory<>("nomParticipant"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDateInscription.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Styliser la colonne statut
        colStatut.setCellFactory(column -> new TableCell<Inscription, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    switch (statut) {
                        case "Confirmé":
                            setStyle("-fx-background-color:#d4edda; -fx-text-fill: #155724;");
                            break;
                        case "En attente":
                            setStyle("-fx-background-color:#fff3cd; -fx-text-fill: #856404;");
                            break;
                        case "Annulé":
                            setStyle("-fx-background-color:#f8d7da; -fx-text-fill: #721c24;");
                            break;
                    }
                }
            }
        });

        tableInscriptions.setItems(listeInscriptions);

        // Initialiser les ComboBox
        chargerEvenementsExemples();
        initialiserStatuts();

        // Désactiver le bouton Modifier au démarrage
        if (btnModifier != null) {
            btnModifier.setDisable(true);
        }

        mettreAJourCompteur();
        afficherStatus("Prêt - Ajoutez une nouvelle inscription");
        System.out.println("✅ Initialisation terminée");
    }
    private void chargerEvenementsExemples() {
// Exemples d'événements (à remplacer par vos vraies données)
        ObservableList<String> evenements =
                FXCollections.observableArrayList(

                        "Conférence Annuelle 2024",
                        "Atelier de Formation Java",
                        "Séminaire Marketing Digital",
                        "Journée Portes Ouvertes",
                        "Gala de Charité"

                );
        comboEvenement.setItems(evenements);
// Ajouter "Tous" au filtre
        ObservableList<String> evenementsAvecTous =
                FXCollections.observableArrayList("Tous");
        evenementsAvecTous.addAll(evenements);
        comboFiltreEvenement.setItems(evenementsAvecTous);
        comboFiltreEvenement.setValue("Tous");
    }
    private void initialiserStatuts() {
        ObservableList<String> statuts =
                FXCollections.observableArrayList(

                        "Confirmé", "En attente", "Annulé"

                );
        comboStatut.setItems(statuts);

        comboStatut.setValue("Confirmé");
    }
    @FXML
    public void ajouterInscription() {
        System.out.println("Méthode ajouterInscription appelée");
        if (!validerFormulaire()) {
            return;
        }
        Inscription nouvelleInscription = new Inscription(

                prochainId++,
                comboEvenement.getValue(),
                txtNomParticipant.getText().trim(),
                txtEmail.getText().trim(),
                txtTelephone.getText().trim(),

                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),

        comboStatut.getValue(),
                txtNotes.getText().trim()

);
        listeInscriptionsComplete.add(nouvelleInscription);
        appliquerFiltre();
        reinitialiserFormulaire();
        mettreAJourCompteur();
        afficherStatus("✓ Inscription ajoutée avec succès !");
        afficherAlerte(Alert.AlertType.INFORMATION, "Succès",

                "L'inscription de " +
                        nouvelleInscription.getNomParticipant() +
                        " à l'événement \"" +

                        nouvelleInscription.getEvenement() + "\" a été enregistrée.");
    }
    @FXML
    public void modifierInscription() {
        System.out.println("Méthode modifierInscription appelée");
        Inscription inscriptionSelectionnee =
                tableInscriptions.getSelectionModel().getSelectedItem();
        if (inscriptionSelectionnee == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une inscription à modifier.");

            return;
        }
        if (!validerFormulaire()) {
            return;
        }

        inscriptionSelectionnee.setEvenement(comboEvenement.getValue());
        inscriptionSelectionnee.setNomParticipant(txtNomParticipant.getText().trim());
        inscriptionSelectionnee.setEmail(txtEmail.getText().trim());
        inscriptionSelectionnee.setTelephone(txtTelephone.getText().trim()
        );
        inscriptionSelectionnee.setStatut(comboStatut.getValue());
        inscriptionSelectionnee.setNotes(txtNotes.getText().trim());
        tableInscriptions.refresh();
        reinitialiserFormulaire();
        afficherStatus("✓ Inscription modifiée avec succès !");
        afficherAlerte(Alert.AlertType.INFORMATION, "Succès",

                "L'inscription a été modifiée.");

    }
    @FXML
    public void supprimerInscription() {
        System.out.println("Méthode supprimerInscription appelée");
        Inscription inscriptionSelectionnee =
                tableInscriptions.getSelectionModel().getSelectedItem();
        if (inscriptionSelectionnee == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Attention",
                    "Veuillez sélectionner une inscription à supprimer.");
            return;
        }
        Optional<ButtonType> resultat = afficherConfirmation(

                "Confirmation de suppression",
                "Voulez-vous vraiment supprimer l'inscription de "

                        +

                        inscriptionSelectionnee.getNomParticipant()

                        + " ?"
        );
        if (resultat.isPresent() && resultat.get() ==
                ButtonType.OK) {
            listeInscriptionsComplete.remove(inscriptionSelectionnee);

            appliquerFiltre();
            reinitialiserFormulaire();
            mettreAJourCompteur();
            afficherStatus("✓ Inscription supprimée");
        }
    }
    @FXML
    public void selectionnerInscription() {
        System.out.println("Méthode selectionnerInscription appelée");
                Inscription inscriptionSelectionnee =
                        tableInscriptions.getSelectionModel().getSelectedItem();
        if (inscriptionSelectionnee != null) {
            comboEvenement.setValue(inscriptionSelectionnee.getEvenement());
            txtNomParticipant.setText(inscriptionSelectionnee.getNomParticipant());

            txtEmail.setText(inscriptionSelectionnee.getEmail());
            txtTelephone.setText(inscriptionSelectionnee.getTelephone());
            comboStatut.setValue(inscriptionSelectionnee.getStatut());
            txtNotes.setText(inscriptionSelectionnee.getNotes());
            if (btnModifier != null) {
                btnModifier.setDisable(false);
            }
            afficherStatus("Inscription sélectionnée - Vous pouvez la modifier");
        }
    }
    @FXML
    public void reinitialiserFormulaire() {
        System.out.println("Méthode reinitialiserFormulaire appelée");

                comboEvenement.setValue(null);
        txtNomParticipant.clear();
        txtEmail.clear();
        txtTelephone.clear();
        comboStatut.setValue("Confirmé");
        txtNotes.clear();
        tableInscriptions.getSelectionModel().clearSelection();
        if (btnModifier != null) {
            btnModifier.setDisable(true);
        }
        afficherStatus("Formulaire réinitialisé");
    }
    @FXML
    public void filtrerInscriptions() {
        System.out.println("Méthode filtrerInscriptions appelée");
        appliquerFiltre();
    }
    private void appliquerFiltre() {
        String filtreEvenement = comboFiltreEvenement.getValue();
        listeInscriptions.clear();
        if (filtreEvenement == null ||
                filtreEvenement.equals("Tous")) {

            listeInscriptions.addAll(listeInscriptionsComplete);
        } else {
            for (Inscription inscription :

                    listeInscriptionsComplete) {

                if

                (inscription.getEvenement().equals(filtreEvenement)) {
                    listeInscriptions.add(inscription);
                }
            }
        }
        mettreAJourCompteur();
        afficherStatus("Filtre appliqué: " +
                listeInscriptions.size() + " inscription(s)");
    }
    @FXML
    public void actualiser() {
        System.out.println("Méthode actualiser appelée");
        appliquerFiltre();

        afficherStatus("✓ Liste actualisée");
    }
    @FXML
    public void fermer() {
        System.out.println("Méthode fermer appelée");
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();
        if (comboEvenement.getValue() == null ||
                comboEvenement.getValue().trim().isEmpty()) {

            erreurs.append("- L'événement est obligatoire\n");
        }
        if (txtNomParticipant.getText().trim().isEmpty()) {
            erreurs.append("- Le nom du participant est obligatoire\n");
        }
        if (txtEmail.getText().trim().isEmpty()) {
            erreurs.append("- L'email est obligatoire\n");
        } else if
        (!EMAIL_PATTERN.matcher(txtEmail.getText().trim()).matches()) {
            erreurs.append("- L'email n'est pas valide\n");
        }
        if (txtTelephone.getText().trim().isEmpty()) {
            erreurs.append("- Le téléphone est obligatoire\n");
        }
        if (comboStatut.getValue() == null ||
                comboStatut.getValue().trim().isEmpty()) {

            erreurs.append("- Le statut est obligatoire\n");
        }
        if (erreurs.length() > 0) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreurs de validation", erreurs.toString());

            return false;
        }
        return true;
    }

    private void mettreAJourCompteur() {
        if (lblNombreInscriptions != null) {
            int total = listeInscriptions.size();
            lblNombreInscriptions.setText("Total: " + total + " inscription" + (total > 1 ? "s" : ""));
        }
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
    // Classe interne pour représenter une inscription
    public static class Inscription {
        private int id;
        private String evenement;
        private String nomParticipant;
        private String email;
        private String telephone;
        private String dateInscription;
        private String statut;
        private String notes;
        public Inscription(int id, String evenement, String
                                   nomParticipant, String email,

                           String telephone, String
                                   dateInscription, String statut, String notes) {

            this.id = id;

            this.evenement = evenement;
            this.nomParticipant = nomParticipant;
            this.email = email;
            this.telephone = telephone;
            this.dateInscription = dateInscription;
            this.statut = statut;
            this.notes = notes;
        }
        // Getters
        public int getId() { return id; }
        public String getEvenement() { return evenement; }
        public String getNomParticipant() { return nomParticipant;
        }
        public String getEmail() { return email; }
        public String getTelephone() { return telephone; }
        public String getDateInscription() { return
                dateInscription; }
        public String getStatut() { return statut; }
        public String getNotes() { return notes; }
        // Setters
        public void setId(int id) { this.id = id; }
        public void setEvenement(String evenement) { this.evenement
                = evenement; }
        public void setNomParticipant(String nomParticipant) {
            this.nomParticipant = nomParticipant; }
        public void setEmail(String email) { this.email = email; }
        public void setTelephone(String telephone) { this.telephone
                = telephone; }
        public void setDateInscription(String dateInscription) {
            this.dateInscription = dateInscription; }
        public void setStatut(String statut) { this.statut =
                statut; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
