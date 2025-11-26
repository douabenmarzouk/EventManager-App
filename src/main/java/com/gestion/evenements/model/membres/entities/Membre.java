// ============ FICHIER 2: Membre.java (CORRIGÉ ET COMPLÉTÉ) ============
package com.gestion.evenements.model.membres.entities;

import com.gestion.evenements.model.membres.enums.StatutMembre;
import com.gestion.evenements.model.membres.exceptions.MembreException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Membre extends Personne {
    private StatutMembre statut;
    private boolean cotisationPayee;
    private double montantCotisation;
    private String numeroMembre;
    private LocalDate datePaiementCotisation;
    private List<String> historiqueInscriptions;

    // ---------- CONSTRUCTEURS ----------
    public Membre() {
        super();
        this.statut = StatutMembre.INACTIF;
        this.cotisationPayee = false;
        this.montantCotisation = 50.0; // Montant par défaut
        this.numeroMembre = genererNumeroMembre();
        this.historiqueInscriptions = new ArrayList<>();
    }

    public Membre(int id, String nom, String prenom, String email, String telephone) {
        super(id, nom, prenom, email, telephone);
        this.statut = StatutMembre.INACTIF;
        this.cotisationPayee = false;
        this.montantCotisation = 50.0;
        this.numeroMembre = genererNumeroMembre();
        this.historiqueInscriptions = new ArrayList<>();
    }

    public Membre(int id, String nom, String prenom, String email, String telephone,
                  boolean cotisationPayee) {
        super(id, nom, prenom, email, telephone);
        this.cotisationPayee = cotisationPayee;
        this.montantCotisation = 50.0;
        this.statut = cotisationPayee ? StatutMembre.ACTIF : StatutMembre.INACTIF;
        this.numeroMembre = genererNumeroMembre();
        this.historiqueInscriptions = new ArrayList<>();
    }

    // ---------- MÉTHODES PRINCIPALES ----------

    @Override
    public String getRole() {
        return "MEMBRE";
    }

    /**
     * Inscrire le membre à un événement
     */
    public void inscrireEvenement(String nomEvenement) throws MembreException {
        if (!peutSinscrire()) {
            throw new MembreException(
                    "Cotisation non payée. Veuillez payer votre cotisation.",
                    "COTISATION_NON_PAYEE"
            );
        }
        historiqueInscriptions.add(nomEvenement + " - " + LocalDate.now());
        System.out.println("✅ " + getNomComplet() + " s'est inscrit à l'événement : " + nomEvenement);
    }

    /**
     * Payer la cotisation
     */
    public void payerCotisation(double montant) throws MembreException {
        if (this.cotisationPayee) {
            throw new MembreException(
                    "Cotisation déjà payée",
                    "COTISATION_DEJA_PAYEE"
            );
        }
        this.cotisationPayee = true;
        this.montantCotisation = montant;
        this.datePaiementCotisation = LocalDate.now();
        this.statut = StatutMembre.ACTIF;
        System.out.println("✅ Cotisation payée avec succès pour " + getNomComplet());
        System.out.println("   Montant : " + montant + "€");
        System.out.println("   Date : " + datePaiementCotisation);
    }

    /**
     * Surcharge de payerCotisation pour les appels sans paramètre
     */
    public void payerCotisation() {
        this.cotisationPayee = true;
        this.datePaiementCotisation = LocalDate.now();
        this.statut = StatutMembre.ACTIF;
        System.out.println("✅ Cotisation payée avec succès pour " + getNomComplet());
        System.out.println("   Date de paiement : " + datePaiementCotisation);
        System.out.println("   Statut changé en : " + statut.getLibelle());
    }

    /**
     * Consulter l'historique des inscriptions
     */
    public void consulterHistorique() {
        System.out.println("\n=== Historique des inscriptions de " + getNomComplet() + " ===");
        System.out.println("Numéro de membre : " + numeroMembre);
        if (historiqueInscriptions.isEmpty()) {
            System.out.println("Aucune inscription pour le moment.");
        } else {
            for (int i = 0; i < historiqueInscriptions.size(); i++) {
                System.out.println((i + 1) + ". " + historiqueInscriptions.get(i));
            }
        }
        System.out.println("Total : " + historiqueInscriptions.size() + " inscription(s)");
    }

    /**
     * Suspendre le membre
     */
    public void suspendre(String raison) {
        this.statut = StatutMembre.SUSPENDU;
        System.out.println("⚠️ Membre " + getNomComplet() + " suspendu.");
        System.out.println("   Raison : " + raison);
    }

    /**
     * Réactiver le membre
     */
    public void reactiver() throws MembreException {
        if (!cotisationPayee) {
            throw new MembreException(
                    "Impossible de réactiver : cotisation non payée.",
                    "COTISATION_NON_PAYEE"
            );
        }
        this.statut = StatutMembre.ACTIF;
        System.out.println("✅ Membre " + getNomComplet() + " réactivé avec succès.");
    }

    /**
     * Vérifier si le membre peut s'inscrire
     */
    public boolean peutSinscrire() {
        return statut == StatutMembre.ACTIF && cotisationPayee;
    }

    /**
     * Générer un numéro de membre unique
     */
    private String genererNumeroMembre() {
        return "M" + System.currentTimeMillis();
    }

    /**
     * Afficher les informations du membre
     */
    @Override
    public void afficherInfos() {
        super.afficherInfos();
        System.out.println("--- Informations Membre ---");
        System.out.println("Numéro de membre : " + numeroMembre);
        System.out.println("Statut : " + statut.getLibelle());
        System.out.println("Cotisation payée : " + (cotisationPayee ? "✅ Oui" : "❌ Non"));
        System.out.println("Montant cotisation : " + montantCotisation + "€");
        if (datePaiementCotisation != null) {
            System.out.println("Date de paiement : " + datePaiementCotisation);
        }
        System.out.println("Nombre d'inscriptions : " + historiqueInscriptions.size());
        System.out.println("Peut s'inscrire : " + (peutSinscrire() ? "✅ Oui" : "❌ Non"));
    }

    // ---------- GETTERS & SETTERS ----------
    public StatutMembre getStatut() { return statut; }
    public void setStatut(StatutMembre statut) { this.statut = statut; }

    public boolean isCotisationPayee() { return cotisationPayee; }
    public void setCotisationPayee(boolean cotisationPayee) { this.cotisationPayee = cotisationPayee; }

    public double getMontantCotisation() { return montantCotisation; }
    public void setMontantCotisation(double montantCotisation) { this.montantCotisation = montantCotisation; }

    public String getNumeroMembre() { return numeroMembre; }
    public void setNumeroMembre(String numeroMembre) { this.numeroMembre = numeroMembre; }

    public LocalDate getDatePaiementCotisation() { return datePaiementCotisation; }
    public void setDatePaiementCotisation(LocalDate datePaiementCotisation) {
        this.datePaiementCotisation = datePaiementCotisation;
    }

    public List<String> getHistoriqueInscriptions() {
        return new ArrayList<>(historiqueInscriptions);
    }

    @Override
    public String toString() {
        return "Membre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", numeroMembre='" + numeroMembre + '\'' +
                ", statut=" + statut.getLibelle() +
                ", cotisationPayee=" + cotisationPayee +
                ", montant=" + montantCotisation + "€" +
                '}';
    }
}
