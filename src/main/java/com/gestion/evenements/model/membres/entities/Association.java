package com.gestion.evenements.model.membres.entities;

import com.gestion.evenements.model.membres.exceptions.MembreException;
import com.gestion.evenements.model.membres.record.Feedback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class Association {

    private int id;
    private String nom;
    private String description;
    private String localisation;
    private String email;
    private LocalDate dateCreation;   // LocalDate, pas Date !
    private double budget;
    private boolean active;
    private double solde = 0.0;

    private final ObservableList<Membre> membreList = FXCollections.observableArrayList();

    public Association() {}

    public Association(int id, String nom, String description, String localisation,
                       String email, LocalDate dateCreation, double budget, boolean active) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.localisation = localisation;
        this.email = email;
        this.dateCreation = dateCreation;
        this.budget = budget;
        this.active = active;
    }

    // ==================== GETTERS & SETTERS ====================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }   // INDISPENSABLE

    public ObservableList<Membre> getMembres() { return membreList; }

    // ==================== GESTION MEMBRES ====================
    public void ajouterMembre(Membre membre) throws MembreException {
        if (membre == null) throw new MembreException("Membre null", "MEMBRE_NULL");
        if (membreList.stream().anyMatch(m -> m.getId() == membre.getId()))
            throw new MembreException("Membre déjà existant", "DUPLICATE");
        membreList.add(membre);
    }

    public void supprimerMembre(int id) throws MembreException {
        if (!membreList.removeIf(m -> m.getId() == id))
            throw new MembreException("Membre non trouvé", "NOT_FOUND");
    }

    public void recevoirCotisation(int idMembre, double montant) throws MembreException {
        if (montant <= 0) throw new MembreException("Montant invalide", "MONTANT_NEGATIF");
        Membre m = membreList.stream()
                .filter(mem -> mem.getId() == idMembre)
                .findFirst()
                .orElseThrow(() -> new MembreException("Membre introuvable", "NOT_FOUND"));

        if (m.isCotisationPayee()) throw new MembreException("Déjà payée", "PAYEE");

        m.payerCotisation(montant);
        this.solde += montant;
    }

    public long getNombreTotalMembres() { return membreList.size(); }

    public long getNombreMembresPayes() {
        return membreList.stream().filter(Membre::isCotisationPayee).count();
    }

    public long getNombreFeedbacks() {                         // MÉTHODE AJOUTÉE
        return getAllFeedbacks().size();
    }

    public double getTotalCotisationsRecoltees() { return solde; }

    public List<Feedback> getAllFeedbacks() {
        return membreList.stream()
                .flatMap(m -> m.getFeedbacks().stream())
                .sorted(Comparator.comparing(Feedback::date).reversed())

                .toList();
    }
    /**
     * Méthode conservée pour compatibilité avec ton ancien code
     * Met à jour uniquement le solde (moins sécurisé, mais rapide)
     */

    @Override
    public String toString() {
        return "Association{id=" + id + ", nom='" + nom + "', solde=" + solde + ", membres=" + membreList.size() + '}';
    }
}