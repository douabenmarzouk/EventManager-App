package com.gestion.evenements.view.evenements;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class RapportController {
    // Filtres
    @FXML private ComboBox<String> comboPeriode;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<String> comboStatut;
    // Labels de statistiques
    @FXML private Label lblTotalEvenements;
    @FXML private Label lblTotalInscriptions;
    @FXML private Label lblCapaciteTotale;
    @FXML private Label lblTauxRemplissage;
    // Labels de progression
    @FXML private Label lblConfirmes;
    @FXML private Label lblEnAttente;
    @FXML private Label lblAnnules;
    // Barres de progression
    @FXML private ProgressBar progressConfirmes;
    @FXML private ProgressBar progressEnAttente;
    @FXML private ProgressBar progressAnnules;
    // Table
    @FXML private TableView<RapportEvenement> tableRapports;

    @FXML private TableColumn<RapportEvenement, String>
            colEvenement;
    @FXML private TableColumn<RapportEvenement, String> colDate;
    @FXML private TableColumn<RapportEvenement, Integer>
            colInscrits;
    @FXML private TableColumn<RapportEvenement, Integer>
            colCapacite;
    @FXML private TableColumn<RapportEvenement, Integer>
            colDisponible;
    @FXML private TableColumn<RapportEvenement, String> colTaux;
    @FXML private TableColumn<RapportEvenement, String> colStatut;
    // Autres
    @FXML private Label lblStatus;
    @FXML private Button btnFermer;
    // Données
    private ObservableList<RapportEvenement> listeRapports;
    private ObservableList<RapportEvenement> listeRapportsComplete;
    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur de rapports...");
// Initialiser les listes
        listeRapportsComplete = FXCollections.observableArrayList();
        listeRapports = FXCollections.observableArrayList();
// Configurer les colonnes
        colEvenement.setCellValueFactory(new
                PropertyValueFactory<>("nomEvenement"));
        colDate.setCellValueFactory(new
                PropertyValueFactory<>("date"));
        colInscrits.setCellValueFactory(new
                PropertyValueFactory<>("nombreInscrits"));
        colCapacite.setCellValueFactory(new
                PropertyValueFactory<>("capacite"));
        colDisponible.setCellValueFactory(new
                PropertyValueFactory<>("placesDisponibles"));
        colTaux.setCellValueFactory(new
                PropertyValueFactory<>("tauxRemplissage"));
        colStatut.setCellValueFactory(new
                PropertyValueFactory<>("statut"));
// Styliser les colonnes
        colTaux.setCellFactory(column -> new
                TableCell<RapportEvenement, String>() {

                    @Override
                    protected void updateItem(String taux, boolean empty) {
                        super.updateItem(taux, empty);
                        if (empty || taux == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(taux);
                            double pourcentage =
                                    Double.parseDouble(taux.replace("%", ""));
                            if (pourcentage >= 80) {
                                setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                            } else if (pourcentage >= 50) {
                                setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");

                            } else {
                                setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");

                            }
                        }
                    }
                });
        colStatut.setCellFactory(column -> new
                TableCell<RapportEvenement, String>() {

                    @Override
                    protected void updateItem(String statut, boolean empty)

                    {

                        super.updateItem(statut, empty);
                        if (empty || statut == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(statut);
                            switch (statut) {
                                case "Complet":
                                    setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

                                    break;
                                case "Presque complet":
                                    setStyle("-fx-background-color:#f39c12; -fx-text-fill: white; -fx-font-weight: bold;");

                                    break;
                                case "Disponible":
                                    setStyle("-fx-background-color:#2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

                                    break;

                            }
                        }
                    }
                });
        tableRapports.setItems(listeRapports);
// Initialiser les filtres
        initialiserFiltres();
// Charger des données d'exemple
        chargerDonneesExemples();
// Calculer les statistiques
        calculerStatistiques();
        afficherStatus("Rapport généré avec succès");
        System.out.println("Initialisation terminée");
    }
    private void initialiserFiltres() {
// Périodes
        ObservableList<String> periodes =
                FXCollections.observableArrayList(

                        "Aujourd'hui", "Cette semaine", "Ce mois", "Ce trimestre", "Cette année", "Tout", "Personnalisé"
                );
        comboPeriode.setItems(periodes);
        comboPeriode.setValue("Tout");
// Statuts
        ObservableList<String> statuts =
                FXCollections.observableArrayList(

                        "Tous", "Disponible", "Presque complet", "Complet"

                );
        comboStatut.setItems(statuts);
        comboStatut.setValue("Tous");
    }
    private void chargerDonneesExemples() {
// Exemples de données (à remplacer par vos vraies données)
        listeRapportsComplete.add(new RapportEvenement(

                "Conférence Annuelle 2024", "15/12/2024", 145, 150,

                5, "96.67%", "Presque complet"
        ));
        listeRapportsComplete.add(new RapportEvenement(

                "Atelier de Formation Java", "20/12/2024", 30, 50,

                20, "60.00%", "Disponible"

        ));
        listeRapportsComplete.add(new RapportEvenement(

                "Séminaire Marketing Digital", "10/01/2025", 200,

                200, 0, "100.00%", "Complet"
        ));
        listeRapportsComplete.add(new RapportEvenement(

                "Journée Portes Ouvertes", "25/01/2025", 80, 100,

                20, "80.00%", "Presque complet"
        ));
        listeRapportsComplete.add(new RapportEvenement(

                "Gala de Charité", "05/02/2025", 120, 300, 180,

                "40.00%", "Disponible"
        ));
        listeRapports.addAll(listeRapportsComplete);
    }
    @FXML
    public void appliquerFiltres() {
        System.out.println("Application des filtres...");
        listeRapports.clear();
        String periode = comboPeriode.getValue();
        String statutFiltre = comboStatut.getValue();
        for (RapportEvenement rapport : listeRapportsComplete) {
            boolean correspond = true;
// Filtre par statut
            if (statutFiltre != null &&
                    !statutFiltre.equals("Tous")) {

                if (!rapport.getStatut().equals(statutFiltre)) {
                    correspond = false;
                }
            }
// Filtre par période personnalisée
            if (periode != null && periode.equals("Personnalisé"))

            {

                if (dateDebut.getValue() != null ||

                        dateFin.getValue() != null) {

// Logique de filtrage par date (nécessite conversion de la date du rapport)

// Pour l'exemple, on garde tout
                }
            }

            if (correspond) {
                listeRapports.add(rapport);
            }
        }
        calculerStatistiques();
        afficherStatus("Filtres appliqués - " +
                listeRapports.size() + " événement(s)");
    }
    @FXML
    public void reinitialiserFiltres() {
        System.out.println("Réinitialisation des filtres...");
        comboPeriode.setValue("Tout");
        comboStatut.setValue("Tous");
        dateDebut.setValue(null);
        dateFin.setValue(null);
        listeRapports.clear();
        listeRapports.addAll(listeRapportsComplete);
        calculerStatistiques();
        afficherStatus("Filtres réinitialisés");
    }
    private void calculerStatistiques() {
        int totalEvenements = listeRapports.size();
        int totalInscriptions = 0;
        int capaciteTotale = 0;
        int confirmes = 0;
        int enAttente = 0;
        int annules = 0;
        for (RapportEvenement rapport : listeRapports) {
            totalInscriptions += rapport.getNombreInscrits();
            capaciteTotale += rapport.getCapacite();
        }
// Calcul du taux de remplissage
        double tauxRemplissage = capaciteTotale > 0 ?

                (double) totalInscriptions / capaciteTotale * 100 :

                0;
// Pour l'exemple, répartition fictive des statutsd'inscription
        confirmes = (int) (totalInscriptions * 0.7);
        enAttente = (int) (totalInscriptions * 0.2);

        annules = totalInscriptions - confirmes - enAttente;
// Mise à jour des labels
        lblTotalEvenements.setText(String.valueOf(totalEvenements));
        lblTotalInscriptions.setText(String.valueOf(totalInscriptions));
        lblCapaciteTotale.setText(String.valueOf(capaciteTotale));
        lblTauxRemplissage.setText(String.format("%.1f%%",
                tauxRemplissage));
// Mise à jour des statistiques par statut
        lblConfirmes.setText(String.valueOf(confirmes));
        lblEnAttente.setText(String.valueOf(enAttente));
        lblAnnules.setText(String.valueOf(annules));
// Mise à jour des barres de progression
        if (totalInscriptions > 0) {
            progressConfirmes.setProgress((double) confirmes /

                    totalInscriptions);

            progressEnAttente.setProgress((double) enAttente /

                    totalInscriptions);

            progressAnnules.setProgress((double) annules /

                    totalInscriptions);
        } else {
            progressConfirmes.setProgress(0);
            progressEnAttente.setProgress(0);
            progressAnnules.setProgress(0);
        }
    }
    @FXML
    public void exporterPDF() {
        System.out.println("Exportation en PDF...");
        afficherAlerte(Alert.AlertType.INFORMATION, "Export PDF",

                "Fonctionnalité d'export PDF en cours de développement.\n" +

                "Utilisez une bibliothèque comme iText ou Apache PDFBox pour implémenter cette fonctionnalité.");
    }
    @FXML
    public void exporterExcel() {
        System.out.println("Exportation en Excel...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le rapport");
        fileChooser.setInitialFileName("rapport_evenements_" +

                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                ".csv");
        fileChooser.getExtensionFilters().add(

                new FileChooser.ExtensionFilter("Fichiers CSV",

                        "*.csv")
        );
        File file =
                fileChooser.showSaveDialog(btnFermer.getScene().getWindow());
        if (file != null) {
            exporterCSV(file);
        }
    }
    private void exporterCSV(File file) {
        try (FileWriter writer = new FileWriter(file)) {
// En-têtes

            writer.append("Événement,Date,Inscrits,Capacité,Disponible,Taux %,Statut\n");

// Données
            for (RapportEvenement rapport : listeRapports) {
                writer.append(rapport.getNomEvenement()).append(",");
                writer.append(rapport.getDate()).append(",");
                writer.append(String.valueOf(rapport.getNombreInscrits())).append(",");
                        writer.append(String.valueOf(rapport.getCapacite())).append(",");
                writer.append(String.valueOf(rapport.getPlacesDisponibles())).append(",");
                writer.append(rapport.getTauxRemplissage()).append(",");
                writer.append(rapport.getStatut()).append("\n");
            }
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                    "Le rapport a été exporté avec succès :\n" +

                            file.getAbsolutePath());

            afficherStatus("Rapport exporté avec succès");
        } catch (IOException e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",

                    "Erreur lors de l'export du fichier : " +

                            e.getMessage());
        }
    }
    @FXML
    public void actualiser() {
        System.out.println("Actualisation des données...");
        calculerStatistiques();
        tableRapports.refresh();
        afficherStatus("✓ Données actualisées");
    }
    @FXML
    public void fermer() {
        System.out.println("Fermeture du rapport...");
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
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
    // Classe interne pour représenter un rapport d'événement
    public static class RapportEvenement {
        private String nomEvenement;
        private String date;
        private int nombreInscrits;
        private int capacite;
        private int placesDisponibles;
        private String tauxRemplissage;
        private String statut;
        public RapportEvenement(String nomEvenement, String date,
                                int nombreInscrits,

                                int capacite, int

                                        placesDisponibles, String tauxRemplissage, String statut) {

            this.nomEvenement = nomEvenement;
            this.date = date;
            this.nombreInscrits = nombreInscrits;
            this.capacite = capacite;
            this.placesDisponibles = placesDisponibles;
            this.tauxRemplissage = tauxRemplissage;
            this.statut = statut;
        }
        // Getters
        public String getNomEvenement() { return nomEvenement; }
        public String getDate() { return date; }
        public int getNombreInscrits() { return nombreInscrits; }
        public int getCapacite() { return capacite; }
        public int getPlacesDisponibles() { return
                placesDisponibles; }
        public String getTauxRemplissage() { return
                tauxRemplissage; }
        public String getStatut() { return statut; }
        // Setters
        public void setNomEvenement(String nomEvenement) {
            this.nomEvenement = nomEvenement; }
        public void setDate(String date) { this.date = date; }
        public void setNombreInscrits(int nombreInscrits) {
            this.nombreInscrits = nombreInscrits; }
        public void setCapacite(int capacite) { this.capacite =
                capacite; }
        public void setPlacesDisponibles(int placesDisponibles) {
            this.placesDisponibles = placesDisponibles; }
        public void setTauxRemplissage(String tauxRemplissage) {
            this.tauxRemplissage = tauxRemplissage; }
        public void setStatut(String statut) { this.statut =
                statut; }
    }
}
