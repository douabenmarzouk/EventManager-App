package com.gestion.evenements.controller;

import com.gestion.evenements.model.membres.entities.Administrateur;
import com.gestion.evenements.model.membres.entities.Membre;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainDashboardController {

    // Navigation buttons
    @FXML private Button dashboardBtn;
    @FXML private Button evenementsBtn;
    @FXML private Button inscriptionsBtn;
    // Stockage des vues pour navigation rapide
    private Parent adminDashboardView;
    private Parent membreView;


    // Sections conditionnelles
    @FXML private VBox adminSection;
    @FXML private VBox associationSection;

    // Top bar
    @FXML private Label pageTitle;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private TextField globalSearchField;
    @FXML private Button notifBtn;
    @FXML private Label notifBadge;

    // Content area
    @FXML private StackPane contentArea;

    // Statistics
    @FXML private Label statEvenementsLabel;
    @FXML private Label statInscriptionsLabel;
    @FXML private Label statMembresLabel;
    @FXML private Label welcomeMessage;

    // Quick action button
    @FXML private Button quickActionBtn;

    private String currentRole = "MEMBRE";
    private String userEmail = "";
    private Membre currentMembre; // AJOUTÉ

    @FXML
    public void initialize() {
        // Cacher sections au départ
        if (adminSection != null) adminSection.setVisible(false);
        if (associationSection != null) associationSection.setVisible(false);

        updateStatistics();
        updateUserInfo();
        updateUIForRole();

        // Active le bouton Dashboard (juste style)
        setActiveButton(dashboardBtn);

        // Charger uniquement pour MEMBRE

    }



    public void setUserRole(String role) {
        this.currentRole = role;
        updateUIForRole();

        contentArea.getChildren().clear(); // toujours nettoyer avant

        switch(role) {
            case "MEMBRE":
                loadView("/views/MembreEspaceView.fxml");
                break;
            case "ADMIN":
                loadView("/views/AdministrateurView.fxml");
                break;
            case "ASSOCIATION":
                loadView("/views/AssociationView.fxml");
                break;
            default:
                // Optionnel : message par défaut
                System.out.println("Rôle inconnu");
        }
    }
    private void showAdminDashboardChoice() {
        // Crée un petit menu contextuel
        ContextMenu menu = new ContextMenu();

        MenuItem itemAdmin = new MenuItem("Dashboard Administrateur (solde, feedbacks, stats)");
        itemAdmin.setStyle("-fx-text-fill: #1e40af; -fx-font-weight: bold;");
        itemAdmin.setOnAction(e -> loadView("/views/AdministrateurView.fxml"));

        MenuItem itemMembre = new MenuItem("Voir comme un membre");
        itemMembre.setOnAction(e -> loadView("/views/MembreEspaceView.fxml"));

        menu.getItems().addAll(itemAdmin, itemMembre);

        // Affiche le menu sous le bouton
        if (dashboardBtn != null) {
            menu.show(dashboardBtn, javafx.geometry.Side.BOTTOM, 0, 0);
        }
    }


    public void setUserEmail(String email) {
        this.userEmail = email;
        updateUserInfo();
    }

    public void setCurrentMembre(Membre membre) {
        this.currentMembre = membre;
    }


    private void updateUIForRole() {
        String icon = "";
        String roleText = "";

        // Par défaut, cacher toutes les sections
        adminSection.setVisible(false);
        adminSection.setManaged(false);
        associationSection.setVisible(false);
        associationSection.setManaged(false);

        switch (currentRole) {
            case "MEMBRE":
                icon = "👤";
                roleText = "Membre";
                quickActionBtn.setText("📋 Mon Profil");
                welcomeMessage.setText("Bienvenue " + getDisplayName() + " ! Découvrez les événements et inscrivez-vous !");
                break;

            case "ADMIN":
                icon = "🔐";
                roleText = "Administrateur";
                // Afficher la section admin sauf le bouton 'Administrateurs'
                adminSection.setVisible(true);
                adminSection.setManaged(true);
                // Supprimer le bouton "🔐 Administrateurs"
                removeAdminButton("🔐 Administrateurs");
                quickActionBtn.setText("👥 Gérer Membres");
                welcomeMessage.setText("Bienvenue Administrateur " + getDisplayName() + " ! Gérez les membres et les événements.");
                // Charger directement la vue admin
                loadView("/views/AdministrateurView.fxml");
                break;

            case "ASSOCIATION":
                icon = "🏛️";
                roleText = "Association";
                associationSection.setVisible(true);
                associationSection.setManaged(true);
                // Supprimer le bouton "🏛️ Mon Association"
                removeAssociationButton("🏛️ Mon Association");
                quickActionBtn.setText("💰 Gérer Budget");
                welcomeMessage.setText("Bienvenue " + getDisplayName() + " ! Supervisez toutes les activités de l'association.");
                // Charger directement la vue association
                loadView("/views/AssociationView.fxml");
                break;
        }

        userRoleLabel.setText(icon + " " + roleText);
    }
    private void removeAdminButton(String buttonText) {
        adminSection.getChildren().removeIf(node -> node instanceof Button && ((Button) node).getText().equals(buttonText));
    }

    private void removeAssociationButton(String buttonText) {
        associationSection.getChildren().removeIf(node -> node instanceof Button && ((Button) node).getText().equals(buttonText));
    }
    private String getDisplayName() {
        if (currentMembre != null && currentMembre.getNom() != null && !currentMembre.getNom().isBlank()) {
            return currentMembre.getNom();
        } else if (userEmail != null && !userEmail.isBlank()) {
            String[] parts = userEmail.split("@");
            return parts.length > 0 ? parts[0] : "Utilisateur";
        } else {
            return "Utilisateur";
        }
    }


    private void updateUserInfo() {
        if (userEmail == null || userEmail.isBlank() || !userEmail.contains("@")) {
            userNameLabel.setText("Utilisateur");
            return;
        }

        String[] parts = userEmail.split("@");
        if (parts.length == 0 || parts[0].isEmpty()) {
            userNameLabel.setText("Utilisateur");
            return;
        }

        String name = parts[0];
        if (name.length() >= 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        userNameLabel.setText(name);
    }

    private void updateStatistics() {
        // TODO: Récupérer les vraies données depuis la base
        statEvenementsLabel.setText("12");
        statInscriptionsLabel.setText("5");
        statMembresLabel.setText("48");
    }


    // =================== NAVIGATION ===================

    @FXML
    public void onNavigateToDashboard() {
        pageTitle.setText("Tableau de bord");
        setActiveButton(dashboardBtn);

        if ("MEMBRE".equals(currentRole)) {
            loadView("/views/MembreEspaceView.fxml");
        } else {
            // ADMIN + ASSOCIATION → aucun tableau de bord
           loadView("/views/AdministrateurView.fxml");
        }
    }

    private void loadAdminAssociationDashboard() {
        VBox dashboard = new VBox(30);
        dashboard.setAlignment(javafx.geometry.Pos.CENTER);
        dashboard.setStyle("-fx-background-color: #f8fafc; -fx-padding: 60;");

        Label title = new Label("Bienvenue dans l'espace de gestion");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label subtitle = new Label("Tous les outils pour gérer votre evenements");
        subtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #64748b;");

        dashboard.getChildren().addAll(title, subtitle);
        contentArea.getChildren().add(dashboard);
    }



    @FXML
    public void onNavigateToInscriptions() {
        pageTitle.setText("Mes Inscriptions");
        setActiveButton(inscriptionsBtn);
        showInfo("Vue Inscriptions", "Affichage des inscriptions à venir...");
    }

    @FXML
    public void onNavigateToMembres() {
        pageTitle.setText("Gestion des Membres");
        loadView("/views/MembreView.fxml");
    }

    @FXML
    public void onNavigateToAdmins() {
        pageTitle.setText("Gestion des Administrateurs");
        loadView("/views/AdministrateurView.fxml");
    }

    @FXML
    public void onNavigateToRapports() {
        pageTitle.setText("Rapports");
        showInfo("Rapports", "Génération des rapports à venir...");
    }




    @FXML
    public void onNavigateToAssociation() {
        pageTitle.setText("Mon Association");
        loadView("/views/AssociationView.fxml");
    }





    @FXML
    public void onNavigateToHelp() {
        pageTitle.setText("Aide");
        showInfo("Aide", "Documentation à venir...");
    }

    @FXML
    public void onLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        alert.setContentText("Vous devrez vous reconnecter pour accéder à l'application.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                loadLoginPage();
            }
        });
    }

    // =================== ACTIONS RAPIDES ===================

    @FXML
    public void onCreateEvent() {
        showInfo("Créer Événement", "Formulaire de création d'événement à venir...");
    }
    @FXML
    private Label labelBienvenue;

    public void setUserInfo(String nom, String role) {
        if (role.equals("ADMIN")) {
            labelBienvenue.setText("Bienvenue Administrateur " + nom);
        } else if (role.equals("ASSOCIATION")) {
            labelBienvenue.setText("Bienvenue Association " + nom);
        } else {
            labelBienvenue.setText("Bienvenue " + nom);
        }
    }


    @FXML
    public void onQuickAction() {
        switch (currentRole) {
            case "MEMBRE":
                showInfo("Mon Profil", "Affichage du profil à venir...");
                break;
            case "ADMIN":
                onNavigateToMembres();

                break;

        }
    }
    private Administrateur currentAdministrateur = null;

    public void setCurrentAdministrateur(Administrateur admin) {
        this.currentAdministrateur = admin;
    }

    public Administrateur getCurrentAdministrateur() {
        return currentAdministrateur;
    }
    @FXML
    public void onShowNotifications() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Vous avez 3 nouvelles notifications");
        alert.setContentText(
                "• Nouvel événement: Conférence Tech 2025\n" +
                        "• Rappel: Paiement cotisation\n" +
                        "• Message de l'administration"
        );
        alert.showAndWait();
    }

    // =================== UTILITAIRES ===================

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Si c'est l'espace membre, passer le membre
            if (fxmlPath.contains("MembreEspaceView") && currentMembre != null) {
                MembreEspaceController controller = loader.getController();
                controller.setMembre(currentMembre);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur de chargement de la vue: " + fxmlPath);
        }
    }

    private void loadLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion d'Événements - Connexion");
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeBtn) {
        // Réinitialiser tous les boutons
        dashboardBtn.getStyleClass().remove("active-nav");

        // Activer le bouton cliqué
        activeBtn.getStyleClass().add("active-nav");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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
}