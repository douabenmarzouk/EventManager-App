package com.gestion.evenements.controller;

import com.gestion.evenements.database.dao.AssociationDAO;
import com.gestion.evenements.database.dao.PersonneDAO;
import com.gestion.evenements.model.membres.entities.Administrateur;
import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.enums.NiveauAcces;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class InscriptionController {

    // Sélection de rôle
    @FXML private ToggleButton roleMembreBtn;
    @FXML private ToggleButton roleAdminBtn;
    @FXML private ToggleButton roleAssociationBtn;

    // Formulaire de base
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // Indicateur mot de passe
    @FXML private ProgressBar passwordStrengthBar;
    @FXML private Label passwordStrengthLabel;

    // Sections spécifiques
    @FXML private VBox membreSection;
    @FXML private VBox adminSection;
    @FXML private VBox associationSection;
    @FXML private ComboBox<NiveauAcces> niveauAccesCombo;
    @FXML private TextField nomAssociationField;
    @FXML private TextField localisationField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField budgetField;

    // Conditions
    @FXML private CheckBox acceptConditionsCheck;
    @FXML private CheckBox newsletterCheck;

    // Messages
    @FXML
    private Label inscriptionsCountLabel;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button inscrireBtn;
    @FXML private Button goToLoginBtn;

    private ToggleGroup roleGroup;
    private String selectedRole = "";

    private PersonneDAO personneDAO;
    private AssociationDAO associationDAO;

    @FXML
    public void initialize() {
        // Initialiser les DAOs
        personneDAO = new PersonneDAO();
        associationDAO = new AssociationDAO();

        // Groupe de toggle buttons
        roleGroup = new ToggleGroup();
        roleMembreBtn.setToggleGroup(roleGroup);
        roleAdminBtn.setToggleGroup(roleGroup);
        roleAssociationBtn.setToggleGroup(roleGroup);

        // Sélectionner Membre par défaut
        roleMembreBtn.setSelected(true);
        selectedRole = "MEMBRE";

        // Initialiser le combo niveau accès
        niveauAccesCombo.setItems(FXCollections.observableArrayList(NiveauAcces.values()));
        niveauAccesCombo.setValue(NiveauAcces.MODERATEUR);

        // Cacher les messages
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        // Listener pour la force du mot de passe
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> updatePasswordStrength(newVal));

        // Setup hover effects
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
            if (!btn.isSelected()) btn.setStyle(hoverStyle);
        });

        btn.setOnMouseExited(e -> {
            if (!btn.isSelected()) btn.setStyle(normalStyle);
        });
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) goToLoginBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion d'Événements - Connexion");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onRoleSelected() {
        if (roleMembreBtn.isSelected()) {
            selectedRole = "MEMBRE";
            updateToggleStyle(roleMembreBtn, true);
            updateToggleStyle(roleAdminBtn, false);
            updateToggleStyle(roleAssociationBtn, false);
            showSection(membreSection);
        } else if (roleAdminBtn.isSelected()) {
            selectedRole = "ADMIN";
            updateToggleStyle(roleMembreBtn, false);
            updateToggleStyle(roleAdminBtn, true);
            updateToggleStyle(roleAssociationBtn, false);
            showSection(adminSection);
        } else if (roleAssociationBtn.isSelected()) {
            selectedRole = "ASSOCIATION";
            updateToggleStyle(roleMembreBtn, false);
            updateToggleStyle(roleAdminBtn, false);
            updateToggleStyle(roleAssociationBtn, true);
            showSection(associationSection);
        }
        hideMessages();
    }

    private void updateToggleStyle(ToggleButton btn, boolean selected) {
        if (selected) {
            btn.setStyle(
                    "-fx-background-color: #dbeafe; -fx-border-color: #2563eb; -fx-border-radius: 8; " +
                            "-fx-background-radius: 8; -fx-border-width: 2; -fx-text-fill: #1e40af; " +
                            "-fx-font-weight: bold; -fx-cursor: hand;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 8; " +
                            "-fx-background-radius: 8; -fx-border-width: 2; -fx-text-fill: #374151; " +
                            "-fx-font-weight: 600; -fx-cursor: hand;"
            );
        }
    }

    private void showSection(VBox section) {
        membreSection.setVisible(false); membreSection.setManaged(false);
        adminSection.setVisible(false); adminSection.setManaged(false);
        associationSection.setVisible(false); associationSection.setManaged(false);

        section.setVisible(true); section.setManaged(true);
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength / 100.0);

        if (strength < 30) {
            passwordStrengthLabel.setText("Faible");
            passwordStrengthLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        } else if (strength < 60) {
            passwordStrengthLabel.setText("Moyen");
            passwordStrengthLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        } else if (strength < 80) {
            passwordStrengthLabel.setText("Bon");
            passwordStrengthLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
        } else {
            passwordStrengthLabel.setText("Excellent");
            passwordStrengthLabel.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength += 25;
        if (password.length() >= 12) strength += 15;
        if (password.matches(".*[A-Z].*")) strength += 20;
        if (password.matches(".*[a-z].*")) strength += 20;
        if (password.matches(".*[0-9].*")) strength += 20;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength += 20;
        return Math.min(strength, 100);
    }

    @FXML
    public void onInscrire() {
        hideMessages();
        if (!validateFields()) return;

        inscrireBtn.setDisable(true);
        inscrireBtn.setText("Inscription en cours...");

        try {
            boolean success = false;
            switch (selectedRole) {
                case "MEMBRE":
                    success = creerMembre();
                    break;
                case "ADMIN":
                    success = creerAdministrateur();
                    break;
                case "ASSOCIATION":
                    success = creerAssociation();
                    break;
            }

            if (success) {
                showSuccess("✅ Inscription réussie !\nVous allez être redirigé vers la page de connexion...");
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::onRetourLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed") ||
                    e.getMessage().contains("email est déjà utilisé")) {
                showError("❌ Cet email est déjà utilisé !");
            } else {
                showError("❌ Erreur lors de l'inscription: " + e.getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            showError("❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        } finally {
            inscrireBtn.setDisable(false);
            inscrireBtn.setText("✅ S'inscrire");
        }
    }

    private boolean validateFields() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
            showError("❌ Veuillez remplir tous les champs obligatoires !");
            return false;
        }

        if (!isValidEmail(email)) {
            showError("❌ Format d'email invalide !");
            return false;
        }

        if (password.length() < 6) {
            showError("❌ Le mot de passe doit contenir au moins 6 caractères !");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showError("❌ Les mots de passe ne correspondent pas !");
            return false;
        }

        if (!acceptConditionsCheck.isSelected()) {
            showError("❌ Vous devez accepter les conditions générales !");
            return false;
        }

        if (selectedRole.equals("ASSOCIATION")) {
            if (nomAssociationField.getText().trim().isEmpty() ||
                    localisationField.getText().trim().isEmpty()) {
                showError("❌ Veuillez remplir les informations de l'association !");
                return false;
            }
        }
        return true;
    }

    private boolean creerMembre() throws SQLException {
        Membre membre = personneDAO.inscrireMembre(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                telephoneField.getText().trim(),
                passwordField.getText()
        );

        System.out.println("✅ Nouveau membre créé: " + membre.getNomComplet());
        System.out.println("   Numéro de membre: " + membre.getNumeroMembre());
        return true;
    }

    private boolean creerAdministrateur() throws SQLException {
        Administrateur admin = personneDAO.inscrireAdministrateur(
                nomField.getText().trim(),
                prenomField.getText().trim(),
                emailField.getText().trim(),
                telephoneField.getText().trim(),
                passwordField.getText(),
                niveauAccesCombo.getValue()
        );

        System.out.println("✅ Nouvel administrateur créé: " + admin.getNomComplet());
        System.out.println("   Niveau d'accès: " + admin.getNiveau().getLibelle());
        return true;
    }

    private boolean creerAssociation() throws SQLException {
        String description = (descriptionArea != null && descriptionArea.getText() != null)
                ? descriptionArea.getText().trim()
                : "Nouvelle association";

        double budget = 10000.0;
        if (budgetField != null && !budgetField.getText().trim().isEmpty()) {
            try {
                budget = Double.parseDouble(budgetField.getText().trim());
            } catch (NumberFormatException e) {
                showError("❌ Budget invalide !");
                return false;
            }
        }

        Association association = associationDAO.creerAssociation(
                nomAssociationField.getText().trim(),
                description,
                localisationField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                budget
        );

        System.out.println("✅ Nouvelle association créée: " + association.getNom());
        System.out.println("   Budget initial: " + budget + "€");
        return true;
    }

    @FXML
    public void onRetourLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) inscrireBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion d'Événements - Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
}