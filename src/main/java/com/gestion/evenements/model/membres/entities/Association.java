// ============ FICHIER 1: Association.java (CORRIGÉ) ============
package com.gestion.evenements.model.membres.entities;

import com.gestion.evenements.model.membres.exceptions.MembreException;
import com.gestion.evenements.model.membres.record.Feedback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Association {
    private int id;
    private String nom;
    private String description;
    private String localisation;
    private String email;
    private Date dateCreation;
    private double budget;
    private boolean active;
    private final List<Membre> membreList;
    private final List<Feedback> feedbackList;
    private double solde;

    // ---------- CONSTRUCTEURS ----------
    public Association(int id, String nom, String description, String localisation,
                       String email, Date dateCreation, double budget, boolean active) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.localisation = localisation;
        this.email = email;
        this.dateCreation = dateCreation;
        this.budget = budget;
        this.active = active;
        this.membreList = new ArrayList<>();
        this.feedbackList = new ArrayList<>();
        this.solde = 0;
    }

    public Association() {
        this.membreList = new ArrayList<>();
        this.feedbackList = new ArrayList<>();
        this.solde = 0;
    }

    // ---------- GETTERS & SETTERS ----------
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getLocalisation() { return localisation; }
    public String getEmail() { return email; }
    public Date getDateCreation() { return dateCreation; }
    public double getBudget() { return budget; }
    public boolean isActive() { return active; }
    public double getSolde() { return solde; }

    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    public void setEmail(String email) { this.email = email; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    public void setBudget(double budget) { this.budget = budget; }
    public void setActive(boolean active) { this.active = active; }
    public void setSolde(double solde) { this.solde = solde; }

    // ---------- GESTION DES MEMBRES ----------

    /**
     * Ajouter un membre dans l'association
     */
    public void ajouterMembre(Membre membre) throws MembreException {
        if (membre == null) {
            throw new MembreException("Membre null", "MEMBRE_NULL");
        }

        boolean existe = membreList.stream()
                .anyMatch(m -> m.getId() == membre.getId());

        if (existe) {
            throw new MembreException(
                    "Un membre avec l'ID " + membre.getId() + " existe déjà",
                    "MEMBRE_DUPLIQUE"
            );
        }
        membreList.add(membre);
        System.out.println("✅ Membre " + membre.getNomComplet() + " ajouté à l'association");
    }

    /**
     * Supprimer un membre de l'association
     */
    public void supprimerMembre(int id) throws MembreException {
        boolean removed = membreList.removeIf(m -> m.getId() == id);
        if (!removed) {
            throw new MembreException("Aucun membre avec l'ID " + id + " trouvé", "MEMBRE_NON_TROUVE");
        }
        System.out.println("✅ Membre supprimé avec succès");
    }

    /**
     * Trouver un membre par son ID
     */
    public Membre trouverMembre(int idMembre) throws MembreException {
        return membreList.stream()
                .filter(m -> m.getId() == idMembre)
                .findFirst()
                .orElseThrow(() -> new MembreException(
                        "Aucun membre trouvé avec l'ID : " + idMembre,
                        "MEMBRE_NON_TROUVE"
                ));
    }

    /**
     * Recevoir une cotisation d'un membre
     */
    public void recevoircotisation(int idMembre, double montant) throws MembreException {
        Membre membre = trouverMembre(idMembre);

        if (membre.isCotisationPayee()) {
            throw new MembreException(
                    "La cotisation est déjà payée",
                    "COTISATION_DEJA_PAYEE"
            );
        }

        membre.payerCotisation(montant);
        membre.setMontantCotisation(montant);
        this.solde += montant;

        System.out.println("💰 Cotisation reçue de " + membre.getNomComplet());
        System.out.println("   Montant : " + montant + "€");
        System.out.println("   Nouveau solde de l'association : " + this.solde + "€");
    }

    /**
     * Obtenir la liste des membres
     */
    public List<Membre> getMembres() {
        return new ArrayList<>(membreList);
    }

    /**
     * Ajouter un feedback
     */
    public void ajouterFeedback(Feedback f) throws MembreException {
        if (f == null) {
            throw new MembreException("Feedback null", "FEEDBACK_NULL");
        }
        feedbackList.add(f);
    }

    /**
     * Consulter la liste des feedbacks
     */
    public List<Feedback> consulterFeedbacks() {
        return new ArrayList<>(feedbackList);
    }

    /**
     * Afficher les statistiques de l'association
     */
    public void afficherStatistiques() {
        System.out.println("\n=== STATISTIQUES DE L'ASSOCIATION ===");
        System.out.println("Nom : " + nom);
        System.out.println("Nombre de membres : " + membreList.size());
        System.out.println("Membres avec cotisation payée : " +
                membreList.stream().filter(Membre::isCotisationPayee).count());
        System.out.println("Budget initial : " + budget + "€");
        System.out.println("Solde actuel : " + solde + "€");
        System.out.println("========================================\n");
    }

    /**
     * Calculer le budget à partir des cotisations
     */
    public double calculerBudget() throws MembreException {
        if (membreList == null || membreList.isEmpty()) {
            return 0.0;
        }
        return membreList.stream()
                .filter(Membre::isCotisationPayee)
                .mapToDouble(Membre::getMontantCotisation)
                .sum();
    }
}
