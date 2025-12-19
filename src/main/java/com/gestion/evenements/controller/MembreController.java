package com.gestion.evenements.controller;

import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.enums.StatutMembre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.gestion.evenements.utils.UserSession;

import java.util.List;

public class MembreController {

    // =================== CHAMPS FXML ===================
    @FXML private TextField numeroMembreField;
    @FXML private ComboBox<StatutMembre> statutCombo;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField montantCotisationField;
    @FXML private DatePicker datePaiementPicker;
    @FXML private CheckBox cotisationPayeeCheck;
    @FXML private TextField evenementField;

    @FXML private TableView<InscriptionHistorique> historiqueTable;
    @FXML private TableColumn<InscriptionHistorique, Integer> histoNumCol;
    @FXML private TableColumn<InscriptionHistorique, String> histoEvenementCol;
    @FXML private TableColumn<InscriptionHistorique, String> histoDateCol;
    @FXML private Label totalInscriptionsLabel;

    @FXML private TableView<Membre> membresTable;
    @FXML private TableColumn<Membre, Integer> idCol;
    @FXML private TableColumn<Membre, String> numeroCol;
    @FXML private TableColumn<Membre, String> nomCol;
    @FXML private TableColumn<Membre, String> emailCol;
    @FXML private TableColumn<Membre, String> telephoneCol;
    @FXML private TableColumn<Membre, String> statutCol;
    @FXML private TableColumn<Membre, Boolean> cotisationCol;
    @FXML private TableColumn<Membre, Double> montantCol;

    @FXML private TableView<FeedbackDisplay> feedbacksTable;
    @FXML private TableColumn<FeedbackDisplay, String> feedbackMembreCol;
    @FXML private TableColumn<FeedbackDisplay, Integer> feedbackNoteCol;
    @FXML private TableColumn<FeedbackDisplay, String> feedbackCommentaireCol;
    @FXML private TableColumn<FeedbackDisplay, String> feedbackDateCol;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filtreStatutCombo;
    @FXML private Label totalMembresLabel;

    // =================== DONNEES ===================
    private ObservableList<Membre> membresList = FXCollections.observableArrayList();
    private ObservableList<InscriptionHistorique> historiqueList = FXCollections.observableArrayList();
    private ObservableList<FeedbackDisplay> feedbacksList = FXCollections.observableArrayList();
    private Membre currentMembre;

    // =================== INITIALISATION ===================
    @FXML
    public void initialize() {
        if(statutCombo==null || filtreStatutCombo==null || montantCol==null){
            System.err.println("Erreur FXML : certains fx:id ne sont pas injectés correctement !");
            return;
        }

        statutCombo.setItems(FXCollections.observableArrayList(StatutMembre.values()));
        filtreStatutCombo.setItems(FXCollections.observableArrayList("Tous", "ACTIF", "INACTIF", "SUSPENDU"));
        filtreStatutCombo.setValue("Tous");

        // Colonnes membres
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("numeroMembre"));
        nomCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getNomComplet()));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        statutCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getStatut().getLibelle()));
        cotisationCol.setCellValueFactory(new PropertyValueFactory<>("cotisationPayee"));
        montantCol.setCellValueFactory(new PropertyValueFactory<>("montantCotisation"));

        // Colonnes historique
        histoNumCol.setCellValueFactory(new PropertyValueFactory<>("numero"));
        histoEvenementCol.setCellValueFactory(new PropertyValueFactory<>("evenement"));
        histoDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Colonnes feedback
        feedbackMembreCol.setCellValueFactory(new PropertyValueFactory<>("membre"));
        feedbackNoteCol.setCellValueFactory(new PropertyValueFactory<>("note"));
        feedbackCommentaireCol.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        feedbackDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadMembres();
        updateStatistics();

        membresTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> { if(newSel != null) loadMembreDetails(newSel); });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMembres());
        filtreStatutCombo.valueProperty().addListener((obs, oldVal, newVal) -> filterMembres());
    }

    // =================== ACTIONS FXML ===================
    @FXML
    public void onNouveauMembre() {
        clearForm();
    }

    @FXML
    public void onEnregistrer() {
        if (currentMembre == null) {
            // création nouveau membre
            Membre m = new Membre(
                    membresList.size() + 1,
                    nomField.getText(),
                    prenomField.getText(),
                    emailField.getText(),
                    telephoneField.getText(),
                    cotisationPayeeCheck.isSelected()
            );
            membresList.add(m);
            membresTable.setItems(FXCollections.observableArrayList(membresList));
            showSuccess("Membre enregistré avec succès !");
            clearForm();
        } else {
            // mise à jour membre existant
            currentMembre.setNom(nomField.getText());
            currentMembre.setPrenom(prenomField.getText());
            currentMembre.setEmail(emailField.getText());
            currentMembre.setTelephone(telephoneField.getText());
            currentMembre.setCotisationPayee(cotisationPayeeCheck.isSelected());
            membresTable.refresh();
            showSuccess("Membre mis à jour !");
        }
        updateStatistics();
    }

    @FXML
    public void onReinitialiser() {
        clearForm();
    }

    // =================== METHODES INTERNES ===================
    private void loadMembres(){
        membresList.clear();
        membresList.addAll(
                new Membre(1,"Dupont","Jean","jean.dupont@email.com","0612345678",true),
                new Membre(2,"Martin","Marie","marie.martin@email.com","0698765432",false),
                new Membre(3,"Bernard","Paul","paul.bernard@email.com","0687654321",true)
        );
        membresTable.setItems(membresList);
    }

    private void loadMembreDetails(Membre membre){
        currentMembre = membre;
        numeroMembreField.setText(membre.getNumeroMembre());
        statutCombo.setValue(membre.getStatut());
        nomField.setText(membre.getNom());
        prenomField.setText(membre.getPrenom());
        emailField.setText(membre.getEmail());
        telephoneField.setText(membre.getTelephone());
        montantCotisationField.setText(String.valueOf(membre.getMontantCotisation()));
        cotisationPayeeCheck.setSelected(membre.isCotisationPayee());
        datePaiementPicker.setValue(membre.getDatePaiementCotisation());

        loadHistorique(membre);
    }

    private void loadHistorique(Membre membre){
        historiqueList.clear();
        List<String> historique = membre.getHistoriqueInscriptions();
        for(int i=0;i<historique.size();i++){
            String entry = historique.get(i);
            String[] parts = entry.split(" - ",2);
            historiqueList.add(new InscriptionHistorique(i+1, parts[0], parts.length>1?parts[1]:""));
        }
        historiqueTable.setItems(historiqueList);
        totalInscriptionsLabel.setText("Total : "+historique.size()+" inscription(s)");
    }

    private void filterMembres(){
        String search = searchField.getText().toLowerCase();
        String statut = filtreStatutCombo.getValue();
        ObservableList<Membre> filtered = FXCollections.observableArrayList();
        for(Membre m : membresList){
            boolean matchSearch = search.isEmpty() || m.getNom().toLowerCase().contains(search)
                    || m.getPrenom().toLowerCase().contains(search)
                    || m.getEmail().toLowerCase().contains(search);
            boolean matchStatut = statut.equals("Tous") || m.getStatut().name().equals(statut);
            if(matchSearch && matchStatut) filtered.add(m);
        }
        membresTable.setItems(filtered);
        updateStatistics();
    }

    private void updateStatistics(){
        totalMembresLabel.setText("Total : "+membresTable.getItems().size()+" membre(s)");
    }

    private void clearForm(){
        currentMembre=null;
        numeroMembreField.clear(); statutCombo.setValue(null); nomField.clear(); prenomField.clear();
        emailField.clear(); telephoneField.clear(); montantCotisationField.setText("50.0");
        cotisationPayeeCheck.setSelected(false); datePaiementPicker.setValue(null); evenementField.clear();
        historiqueList.clear(); totalInscriptionsLabel.setText("Total : 0 inscription(s)");
    }

    private void showSuccess(String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String msg){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    // =================== CLASSES INTERNES ===================
    public static class InscriptionHistorique{
        private final int numero;
        private final String evenement;
        private final String date;
        public InscriptionHistorique(int numero,String evenement,String date){ this.numero=numero; this.evenement=evenement; this.date=date; }
        public int getNumero(){ return numero; }
        public String getEvenement(){ return evenement; }
        public String getDate(){ return date; }
    }

    public static class FeedbackDisplay{
        private final String membre;
        private final int note;
        private final String commentaire;
        private final String date;
        public FeedbackDisplay(String membre,int note,String commentaire,String date){ this.membre=membre; this.note=note; this.commentaire=commentaire; this.date=date; }
        public String getMembre(){ return membre; }
        public int getNote(){ return note; }
        public String getCommentaire(){ return commentaire; }
        public String getDate(){ return date; }
    }
}
