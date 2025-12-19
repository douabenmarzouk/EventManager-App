package com.gestion.evenements.model.evenements.entities;


import com.gestion.evenements.model.evenements.enums.StatutInscription;
import com.gestion.evenements.model.evenements.exceptions.EvenementException;
import java.time.LocalDateTime;

/**import javax.persistence.*;
 * @Entity
 @Table(name = "inscriptions")**/
public class Inscription {
    /**@Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int id;
     @Column(name = "membre_id", nullable = false)

     private int membreId;
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "evenement_id", nullable = false)
     private Evenement evenement;
     @Column(name = "date_inscription", nullable = false)
     private LocalDateTime dateInscription;
     @Enumerated(EnumType.STRING)
     @Column(length = 20)
     private StatutInscription statut;
     @Column(name = "montant_paye")
     private double montantPaye;
     private boolean confirmed;
     / Constructeur par défaut pour JPA
     public Inscription() {
     this.dateInscription = LocalDateTime.now();
     this.statut = StatutInscription.EN_ATTENTE;
     }**/
    private int id;
    private int membreId;
    private int evenementId;
    private LocalDateTime dateInscription;
    private StatutInscription statut;
    private double montantPaye;
    private boolean confirmed;
    public Inscription(int id, int membreId, int evenementId) {
        this.id = id;
        this.membreId = membreId;
        this.evenementId = evenementId;
        this.dateInscription = LocalDateTime.now();
        this.statut = StatutInscription.EN_ATTENTE;
        this.montantPaye = 0.0;
        this.confirmed = false;
    }
    public void confirmer() throws EvenementException {
        if (statut == StatutInscription.ANNULEE) {
            throw new EvenementException("Impossible de confirmer une inscription annulée !");
        }
        this.statut = StatutInscription.CONFIRMEE;
        this.confirmed = true;
    }

    public void annuler() {
        this.statut = StatutInscription.ANNULEE;
        this.confirmed = false;
    }
    public void validerPaiement(double montant) throws
            EvenementException {
        if (montant <= 0) {
            throw new EvenementException("Montant invalide !");
        }
        this.montantPaye = montant;
        this.statut = StatutInscription.VALIDEE;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMembreId() { return membreId; }
    public void setMembreId(int membreId) { this.membreId =
            membreId; }
    public int getEvenementId() { return evenementId; }
    public void setEvenementId(int evenementId) { this.evenementId
            = evenementId; }
    public LocalDateTime getDateInscription() { return
            dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription; }
    public StatutInscription getStatut() { return statut; }
    public void setStatut(StatutInscription statut) { this.statut =
            statut; }
    public double getMontantPaye() { return montantPaye; }
    public void setMontantPaye(double montantPaye) {
        this.montantPaye = montantPaye; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed =
            confirmed; }
    @Override
    public String toString() {
        return "Inscription{" +
                "id=" + id +

                ", membreId=" + membreId +
                ", evenementId=" + evenementId +
                ", dateInscription=" + dateInscription +
                ", statut=" + statut +
                ", montantPaye=" + montantPaye +
                '}';

    }
}
