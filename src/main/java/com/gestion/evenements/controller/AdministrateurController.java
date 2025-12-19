package com.gestion.evenements.controller;

import com.gestion.evenements.model.membres.entities.Administrateur;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.enums.NiveauAcces;
import com.gestion.evenements.model.membres.exceptions.MembreException;
import com.gestion.evenements.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdministrateurController {


    @FXML private TextField idField;
    @FXML private ComboBox<NiveauAcces> niveauAccesCombo;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNominationPicker;
    @FXML private Label permissionsLabel;
    @FXML private Label niveauAccesLabel;

    // Inscription nouveau membre
    @FXML private TextField newMembreNomField;
    @FXML private TextField newMembrePrenomField;
    @FXML private TextField newMembreEmailField;
    @FXML private TextField newMembreTelField;

    // Validation
    @FXML private TextField idMembreValidationField;

    // Table administrateurs
    @FXML private TableView<Administrateur> administrateursTable;
    @FXML private TableColumn<Administrateur, Integer> adminIdCol;
    @FXML private TableColumn<Administrateur, String> adminNomCol;
    @FXML private TableColumn<Administrateur, String> adminEmailCol;
    @FXML private TableColumn<Administrateur, String> adminTelCol;
    @FXML private TableColumn<Administrateur, String> adminNiveauCol;
    @FXML private TableColumn<Administrateur, LocalDate> adminDateCol;
    @FXML private TableColumn<Administrateur, String> adminPermissionsCol;

    // Journal d'activité
    @FXML private TableView<JournalEntry> journalTable;
    @FXML private TableColumn<JournalEntry, String> journalDateCol;
    @FXML private TableColumn<JournalEntry, String> journalAdminCol;
    @FXML private TableColumn<JournalEntry, String> journalActionCol;
    @FXML private TableColumn<JournalEntry, String> journalDetailsCol;

    // Recherche et filtres
    @FXML private TextField searchAdminField;
    @FXML private ComboBox<String> filtreNiveauCombo;

    // Labels statistiques
    @FXML private Label totalAdminsLabel;
    @FXML private Label statMembresLabel;
    @FXML private Label statAttenteLabel;
    @FXML private Label statValidesLabel;
    @FXML private Label statAdminsLabel;

    // Données
    private ObservableList<Administrateur> adminsList = FXCollections.observableArrayList();
    private ObservableList<JournalEntry> journalList = FXCollections.observableArrayList();
    private Administrateur currentAdmin;

    @FXML
    public void initialize() {
        // Initialiser les ComboBox
        niveauAccesCombo.setItems(FXCollections.observableArrayList(NiveauAcces.values()));
        filtreNiveauCombo.setItems(FXCollections.observableArrayList(
                "Tous", "MODERATEUR", "ADMIN", "SUPER_ADMIN"
        ));
        filtreNiveauCombo.setValue("Tous");

        // Configurer les colonnes administrateurs
        adminIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        adminNomCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomComplet()));
        adminEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        adminTelCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        adminNiveauCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNiveau().getLibelle()));
        adminDateCol.setCellValueFactory(new PropertyValueFactory<>("dateNomination"));
        adminPermissionsCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().peutModifierSysteme() ? "✅ Système" : "⚠️ Limité"));

        // Configurer les colonnes du journal
        journalDateCol.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        journalAdminCol.setCellValueFactory(new PropertyValueFactory<>("administrateur"));
        journalActionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        journalDetailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));

        // Charger les données
        loadAdministrateurs();
        loadJournal();
        updateStatistics();

        // ⭐ NOUVEAU : Sélectionner automatiquement le SUPER_ADMIN par défaut
        setDefaultSuperAdmin();

        // Listeners
        administrateursTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadAdminDetails(newSelection);
                    }
                }
        );

        niveauAccesCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updatePermissionsLabel(newVal);
            }
        });

        searchAdminField.textProperty().addListener((obs, oldVal, newVal) -> filterAdmins());
        filtreNiveauCombo.valueProperty().addListener((obs, oldVal, newVal) -> filterAdmins());
    }

    // ⭐ NOUVELLE MÉTHODE : Définir le Super Admin par défaut
    private void setDefaultSuperAdmin() {
        // Chercher le premier SUPER_ADMIN dans la liste
        Administrateur superAdmin = adminsList.stream()
                .filter(a -> a.getNiveau() == NiveauAcces.SUPER_ADMIN)
                .findFirst()
                .orElse(null);

        if (superAdmin != null) {
            // Sélectionner dans la table
            administrateursTable.getSelectionModel().select(superAdmin);
            // Charger ses détails
            loadAdminDetails(superAdmin);
            // Message de bienvenue
            System.out.println("🔐 Connecté en tant que : " + superAdmin.getNomComplet());
            System.out.println("   Niveau : " + superAdmin.getNiveau().getLibelle());
            System.out.println("   ✅ Vous avez tous les droits d'administration");

            // Ajouter au journal
            ajouterJournal("Connexion", "Session démarrée avec " + superAdmin.getNomComplet());
        } else {
            System.out.println("⚠️ Aucun SUPER_ADMIN trouvé. Veuillez en créer un.");
        }
    }

    private void loadAdministrateurs() {
        // TODO: Charger depuis la base de données
        adminsList.clear();

        Administrateur a1 = new Administrateur(1, "Admin", "Super", "super@admin.com", "0612345678",
                NiveauAcces.SUPER_ADMIN);
        Administrateur a2 = new Administrateur(2, "Modérateur", "Test", "modo@admin.com", "0698765432",
                NiveauAcces.MODERATEUR);

        adminsList.addAll(a1, a2);
        administrateursTable.setItems(adminsList);
    }

    private void loadAdminDetails(Administrateur admin) {
        currentAdmin = admin;

        idField.setText(String.valueOf(admin.getId()));
        niveauAccesCombo.setValue(admin.getNiveau());
        nomField.setText(admin.getNom());
        prenomField.setText(admin.getPrenom());
        emailField.setText(admin.getEmail());
        telephoneField.setText(admin.getTelephone());
        dateNominationPicker.setValue(admin.getDateNomination());

        updatePermissionsLabel(admin.getNiveau());
        updateNiveauAccesLabel(admin.getNiveau());
    }

    private void updatePermissionsLabel(NiveauAcces niveau) {
        String permissions =
                (niveau == NiveauAcces.ADMIN || niveau == NiveauAcces.SUPER_ADMIN)
                        ? "✅ Peut modifier le système"
                        : "⚠️ Permissions limitées";

        permissionsLabel.setText(permissions);
    }


    private void updateNiveauAccesLabel(NiveauAcces niveau) {
        niveauAccesLabel.setText("Niveau: " + niveau.getLibelle());
    }

    private void loadJournal() {
        journalList.clear();

        // Exemples d'entrées de journal
        journalList.add(new JournalEntry(
                LocalDateTime.now().minusHours(2),
                "Admin Super",
                "Création membre",
                "Nouveau membre: Jean Dupont"
        ));
        journalList.add(new JournalEntry(
                LocalDateTime.now().minusHours(5),
                "Modérateur Test",
                "Validation inscription",
                "Membre ID: 15 validé pour événement: Tech 2025"
        ));

        journalTable.setItems(journalList);
    }

    private void filterAdmins() {
        String search = searchAdminField.getText().toLowerCase();
        String niveau = filtreNiveauCombo.getValue();

        ObservableList<Administrateur> filtered = FXCollections.observableArrayList();

        for (Administrateur a : adminsList) {
            boolean matchSearch = search.isEmpty() ||
                    a.getNom().toLowerCase().contains(search) ||
                    a.getPrenom().toLowerCase().contains(search) ||
                    a.getEmail().toLowerCase().contains(search);

            boolean matchNiveau = niveau.equals("Tous") ||
                    a.getNiveau().name().equals(niveau);

            if (matchSearch && matchNiveau) {
                filtered.add(a);
            }
        }

        administrateursTable.setItems(filtered);
        updateStatistics();
    }

    private void updateStatistics() {
        totalAdminsLabel.setText("Total : " + administrateursTable.getItems().size() + " administrateur(s)");
        statAdminsLabel.setText(String.valueOf(adminsList.size()));

        // TODO: Calculer les vraies statistiques
        statMembresLabel.setText("48");
        statAttenteLabel.setText("5");
        statValidesLabel.setText("43");
    }

    // =================== ACTIONS ===================

    @FXML
    public void onNouvelAdmin() {
        clearForm();
        showInfo("Nouvel Administrateur", "Remplissez le formulaire pour créer un nouvel administrateur.");
    }

    @FXML
    public void onEnregistrer() {
        try {
            if (currentAdmin == null) {
                // Créer nouvel admin
                int newId = adminsList.isEmpty() ? 1 :
                        adminsList.stream().mapToInt(Administrateur::getId).max().getAsInt() + 1;

                currentAdmin = new Administrateur(newId, nomField.getText(), prenomField.getText(),
                        emailField.getText(), telephoneField.getText(), niveauAccesCombo.getValue());
            }

            // Mettre à jour
            currentAdmin.setNom(nomField.getText());
            currentAdmin.setPrenom(prenomField.getText());
            currentAdmin.setEmail(emailField.getText());
            currentAdmin.setTelephone(telephoneField.getText());
            currentAdmin.setNiveau(niveauAccesCombo.getValue());
            currentAdmin.setDateNomination(dateNominationPicker.getValue());

            if (!adminsList.contains(currentAdmin)) {
                adminsList.add(currentAdmin);
            }

            administrateursTable.refresh();
            ajouterJournal("Création/Modification admin", "Admin: " + currentAdmin.getNomComplet());
            showSuccess("Administrateur enregistré avec succès !");

        } catch (Exception e) {
            showError("Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    @FXML
    public void onInscrireMembre() {
        if (currentAdmin == null) {
            showError("Veuillez vous connecter en tant qu'administrateur !");
            return;
        }

        String nom = newMembreNomField.getText().trim();
        String prenom = newMembrePrenomField.getText().trim();
        String email = newMembreEmailField.getText().trim();
        String tel = newMembreTelField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showError("Veuillez remplir tous les champs obligatoires !");
            return;
        }

        try {
            Membre nouveau = currentAdmin.inscrireNouveauMembre(nom, prenom, email, tel);

            // Vider les champs
            newMembreNomField.clear();
            newMembrePrenomField.clear();
            newMembreEmailField.clear();
            newMembreTelField.clear();

            ajouterJournal("Inscription membre", "Nouveau membre: " + nouveau.getNomComplet());
            showSuccess("✅ Membre inscrit avec succès !\nNuméro: " + nouveau.getNumeroMembre());

        } catch (MembreException e) {
            showError(e.getMessage());
        }
    }



    @FXML
    public void onGenererRapport() {
        if (currentAdmin != null) {
            currentAdmin.genererRapport();
            ajouterJournal("Génération rapport", "Rapport généré par " + currentAdmin.getNomComplet());
            showSuccess("✅ Rapport généré avec succès !");
        }
    }



    @FXML
    public void onModifierAdmin() {
        Administrateur selected = administrateursTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadAdminDetails(selected);
        } else {
            showError("Veuillez sélectionner un administrateur !");
        }
    }

    @FXML
    public void onSupprimerAdmin() {
        Administrateur selected = administrateursTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Veuillez sélectionner un administrateur !");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer Administrateur");
        alert.setHeaderText("Supprimer " + selected.getNomComplet() + " ?");
        alert.setContentText("Cette action est irréversible !");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                adminsList.remove(selected);
                clearForm();
                ajouterJournal("Suppression admin", "Admin supprimé: " + selected.getNomComplet());
                showSuccess("Administrateur supprimé.");
            }
        });
    }

    @FXML
    public void onActualiserJournal() {
        loadJournal();
        showSuccess("Journal actualisé !");
    }
    @FXML
    private void onGestionGenerale(ActionEvent event) {
        try {
            System.out.println("🔄 Ouverture du Menu Principal...");

            // Utilisez UN POINT au lieu d'un slash
            URL fxmlLocation = getClass().getResource("/fxml.evenements/MenuPrincipal.fxml");

            if (fxmlLocation == null) {
                System.err.println("❌ Fichier FXML introuvable : /fxml.evenements/MenuPrincipal.fxml");
                showError("Le fichier MenuPrincipal.fxml est introuvable!");
                return;
            }

            System.out.println("✅ Fichier trouvé : " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("Menu Principal - Gestion des Événements");

            System.out.println("✅ Menu Principal affiché avec succès!");

        } catch (IOException e) {
            System.err.println("❌ Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
            showError("Impossible de charger le Menu Principal : " + e.getMessage());
        }
    }
    private void debugFichiersFXML() {
        System.out.println("🔍 Recherche des fichiers FXML...");

        String[] chemins = {
                "/fxml/evenements/MenuPrincipal.fxml",
                "/fxml/MenuPrincipal.fxml",
                "/MenuPrincipal.fxml",
                "MenuPrincipal.fxml"
        };

        for (String chemin : chemins) {
            URL url = getClass().getResource(chemin);
            if (url != null) {
                System.out.println("✅ TROUVÉ : " + chemin + " -> " + url);
            } else {
                System.out.println("❌ PAS TROUVÉ : " + chemin);
            }
        }
    }

    @FXML
    public void onEffacerJournal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Effacer Journal");
        alert.setHeaderText("Effacer tout le journal d'activité ?");
        alert.setContentText("Cette action est irréversible !");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                journalList.clear();
                showSuccess("Journal effacé.");
            }
        });
    }

    @FXML
    public void onReinitialiser() {
        clearForm();
    }

    private void clearForm() {
        currentAdmin = null;
        idField.clear();
        niveauAccesCombo.setValue(null);
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
        dateNominationPicker.setValue(LocalDate.now());
        permissionsLabel.setText("Permissions : -");
    }

    private void ajouterJournal(String action, String details) {
        String adminNom = currentAdmin != null ? currentAdmin.getNomComplet() : "Système";
        journalList.add(0, new JournalEntry(LocalDateTime.now(), adminNom, action, details));
        journalTable.refresh();
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
    @FXML
    public void onGererPermissions() {
        try {
            PermissionsController.open(adminsList);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir la fenêtre de gestion des permissions.");
        }
    }




    // Classe interne pour le journal
    public static class JournalEntry {
        private final LocalDateTime dateHeure;
        private final String administrateur;
        private final String action;
        private final String details;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        public JournalEntry(LocalDateTime dateHeure, String administrateur, String action, String details) {
            this.dateHeure = dateHeure;
            this.administrateur = administrateur;
            this.action = action;
            this.details = details;
        }

        public String getDateHeure() { return dateHeure.format(formatter); }
        public String getAdministrateur() { return administrateur; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
    }
}