package com.gestion.evenements.controller;

import com.gestion.evenements.database.dao.AssociationDAO;
import com.gestion.evenements.database.dao.PersonneDAO;
import com.gestion.evenements.model.membres.entities.Administrateur;
import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.entities.Membre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private ToggleButton roleMembreBtn;
    @FXML private ToggleButton roleAdminBtn;
    @FXML private ToggleButton roleAssociationBtn;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;

    private ToggleGroup roleGroup;
    private String selectedRole = "";

    // ✅ DAOs pour l'authentification
    private PersonneDAO personneDAO;
    private AssociationDAO associationDAO;

    @FXML
    public void initialize() {
        // ✅ Initialiser les DAOs
        personneDAO = new PersonneDAO();
        associationDAO = new AssociationDAO();

        // Créer un groupe pour les boutons toggle
        roleGroup = new ToggleGroup();
        roleMembreBtn.setToggleGroup(roleGroup);
        roleAdminBtn.setToggleGroup(roleGroup);
        roleAssociationBtn.setToggleGroup(roleGroup);

        // Sélectionner Membre par défaut
        roleMembreBtn.setSelected(true);
        selectedRole = "MEMBRE";

        // Cacher le message d'erreur
        errorLabel.setVisible(false);

        // Ajouter des styles hover dynamiques
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        addToggleHoverEffect(roleMembreBtn);
        addToggleHoverEffect(roleAdminBtn);
        addToggleHoverEffect(roleAssociationBtn);
    }

    private void addToggleHoverEffect(ToggleButton btn) {
        String normalStyle = btn.getStyle();
        String hoverStyle = normalStyle + " -fx-border-color: #2563eb; -fx-background-color: #eff6ff;";

        btn.setOnMouseEntered(e -> {
            if (!btn.isSelected()) {
                btn.setStyle(hoverStyle);
            }
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isSelected()) {
                btn.setStyle(normalStyle);
            }
        });
    }

    @FXML
    public void onRoleSelected() {
        if (roleMembreBtn.isSelected()) {
            selectedRole = "MEMBRE";
            updateToggleStyle(roleMembreBtn, true);
            updateToggleStyle(roleAdminBtn, false);
            updateToggleStyle(roleAssociationBtn, false);
        } else if (roleAdminBtn.isSelected()) {
            selectedRole = "ADMIN";
            updateToggleStyle(roleMembreBtn, false);
            updateToggleStyle(roleAdminBtn, true);
            updateToggleStyle(roleAssociationBtn, false);
        } else if (roleAssociationBtn.isSelected()) {
            selectedRole = "ASSOCIATION";
            updateToggleStyle(roleMembreBtn, false);
            updateToggleStyle(roleAdminBtn, false);
            updateToggleStyle(roleAssociationBtn, true);
        }
        errorLabel.setVisible(false);
    }

    private void updateToggleStyle(ToggleButton btn, boolean selected) {
        if (selected) {
            btn.setStyle(
                    "-fx-background-color: #dbeafe; " +
                            "-fx-border-color: #2563eb; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-width: 2; " +
                            "-fx-text-fill: #1e40af; " +
                            "-fx-font-weight: bold; " +
                            "-fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-border-color: #e5e7eb; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-width: 2; " +
                            "-fx-text-fill: #374151; " +
                            "-fx-font-weight: 600; " +
                            "-fx-cursor: hand;"
            );
        }
    }

    @FXML
    public void onLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs !");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Email invalide !");
            return;
        }

        if (selectedRole.isEmpty()) {
            showError("Veuillez sélectionner un rôle !");
            return;
        }

        // Désactiver le bouton pendant le chargement
        loginBtn.setDisable(true);
        loginBtn.setText("Connexion...");

        System.out.println("====================================");
        System.out.println("📧 Tentative de connexion");
        System.out.println("====================================");
        System.out.println("Email    : " + email);
        System.out.println("Rôle     : " + selectedRole);
        System.out.println("====================================");

        try {
            // ✅ AUTHENTIFICATION SELON LE RÔLE
            switch (selectedRole) {
                case "MEMBRE":
                    authenticateMembre(email, password);
                    break;
                case "ADMIN":
                    authenticateAdmin(email, password);
                    break;
                case "ASSOCIATION":
                    authenticateAssociation(email, password);
                    break;
            }
        } catch (Exception e) {
            showError("Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
        }
    }

    // ✅ AUTHENTIFICATION MEMBRE
    private void authenticateMembre(String email, String password) throws SQLException, IOException {
        // Vérifier le mot de passe
        if (!personneDAO.verifierMotDePasse(email, password, "MEMBRE")) {
            showError("Email ou mot de passe incorrect !");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
            return;
        }

        // Charger le membre
        Membre membre = personneDAO.getMembreParEmail(email);

        if (membre == null) {
            showError("Membre introuvable !");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
            return;
        }

        System.out.println("✅ Membre authentifié : " + membre.getNomComplet());

        // Charger le dashboard MEMBRE
        loadMembreDashboard(membre);
    }

    // ✅ AUTHENTIFICATION ADMIN
    private void authenticateAdmin(String email, String password) throws SQLException, IOException {
        // Vérifier le mot de passe
        if (!personneDAO.verifierMotDePasse(email, password, "ADMIN")) {
            showError("Email ou mot de passe incorrect !");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
            return;
        }

        // Charger l'administrateur
        Administrateur admin = personneDAO.getAdministrateurParEmail(email);

        if (admin == null) {
            showError("Administrateur introuvable !");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
            return;
        }

        System.out.println("✅ Admin authentifié : " + admin.getNomComplet());
        System.out.println("   Niveau : " + admin.getNiveau().getLibelle());

        // Charger le dashboard ADMIN
        loadAdminDashboard(admin);
    }

    // ✅ AUTHENTIFICATION ASSOCIATION
    private void authenticateAssociation(String email, String password) throws SQLException, IOException {
        // Vérifier le mot de passe
        if (!associationDAO.verifierMotDePasse(email, password)) {
            showError("Email ou mot de passe incorrect !");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
            return;
        }

        // Charger l'association
        Association association = associationDAO.getParEmail(email);

        if (association == null) {
            showError("Association introuvable !");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
            return;
        }

        System.out.println("✅ Association authentifiée : " + association.getNom());
        System.out.println("   Email : " + association.getEmail());
        System.out.println("   Budget : " + association.getBudget() + " €");

        // Charger le dashboard ASSOCIATION
        loadAssociationDashboard(association);
    }

    // ✅ CHARGER LE DASHBOARD MEMBRE
    private void loadMembreDashboard(Membre membre) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MembreEspaceView.fxml"));
        Parent root = loader.load();

        MembreEspaceController controller = loader.getController();
        controller.setMembre(membre);
        controller.setAssociationId(1); // TODO: récupérer le vrai ID depuis la BD

        Stage stage = (Stage) loginBtn.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Espace Membre - " + membre.getNomComplet());
        stage.setMaximized(true);
        stage.show();

        System.out.println("✅ Dashboard Membre chargé\n");
    }

    // ✅ CHARGER LE DASHBOARD ADMIN
    private void loadAdminDashboard(Administrateur admin) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
        Parent root = loader.load();

        MainDashboardController controller = loader.getController();
        controller.setUserEmail(admin.getEmail());
        controller.setUserRole("ADMIN");
        controller.setCurrentAdministrateur(admin);

        Stage stage = (Stage) loginBtn.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Administration - " + admin.getNomComplet());
        stage.setMaximized(true);
        stage.show();

        System.out.println("✅ Dashboard Admin chargé\n");
    }

    // ✅ CHARGER LE DASHBOARD ASSOCIATION
    private void loadAssociationDashboard(Association association) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AssociationView.fxml"));
        Parent root = loader.load();

        AssociationController controller = loader.getController();

        // ✅ PASSER L'ASSOCIATION AU CONTROLLER
        controller.setAssociation(association);

        Stage stage = (Stage) loginBtn.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Gestion Association - " + association.getNom());
        stage.setMaximized(true);
        stage.show();

        System.out.println("✅ Dashboard Association chargé");
        System.out.println("   Nom : " + association.getNom());
        System.out.println("   Solde : " + association.getSolde() + " €");
        System.out.println("====================================\n");
    }

    @FXML
    public void onForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mot de passe oublié");
        alert.setHeaderText("Réinitialisation du mot de passe");
        alert.setContentText(
                "📧 Un email de réinitialisation sera envoyé à votre adresse.\n\n" +
                        "Veuillez vérifier votre boîte de réception dans quelques minutes."
        );
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    public void onSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/InscriptionView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 800);
            stage.setScene(scene);
            stage.setTitle("Gestion d'Événements - Inscription");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page d'inscription");
            alert.setContentText("Erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);

        errorLabel.setStyle(
                "-fx-text-fill: #dc2626; " +
                        "-fx-background-color: #fee2e2; " +
                        "-fx-padding: 10 15; " +
                        "-fx-background-radius: 6; " +
                        "-fx-font-size: 13px; " +
                        "-fx-font-weight: bold;"
        );
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
}