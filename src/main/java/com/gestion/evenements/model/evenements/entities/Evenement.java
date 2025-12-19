package com.gestion.evenements.model.evenements.entities;
import com.gestion.evenements.model.evenements.enums.StatutEvenement;
import com.gestion.evenements.model.evenements.enums.StatutInscription;
import com.gestion.evenements.model.evenements.exceptions.CapaciteDepasseeException;
import com.gestion.evenements.model.evenements.exceptions.EvenementException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**import javax.persistence.*;
 @Entity
 @Table(name = "evenements")**/
public class Evenement implements IEvenement {
    /**@Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int id;
     @Column(nullable = false, length = 200)
     private String titre;
     @Column(length = 1000)
     private String description;
     @Column(name = "date_debut", nullable = false)
     private LocalDateTime dateDebut;
     @Column(name = "date_fin", nullable = false)
     private LocalDateTime dateFin;

     @Column(nullable = false, length = 200)
     private String lieu;
     @Column(name = "capacite_max", nullable = false)
     private int capaciteMax;
     @Column(name = "nb_participants")
     private int nbParticipants;
     @Column(nullable = false)
     private double tarif;
     @Enumerated(EnumType.STRING)
     @Column(length = 20)
     private StatutEvenement statut;
     @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL,
     fetch = FetchType.LAZY)
     private List<Inscription> inscriptions;
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "categorie_id")
     private Categorie categorie;
     // Constructeur par défaut OBLIGATOIRE pour JPA
     public Evenement() {
     this.inscriptions = new ArrayList<>();
     this.nbParticipants = 0;
     this.statut = StatutEvenement.PLANIFIE;
     }**/
    private int id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private int capaciteMax;
    private int nbParticipants;
    private double tarif;
    private StatutEvenement statut;
    private List<Inscription> inscriptions;
    private Categorie categorie;
    public Evenement(int id, String titre, String description,
                     LocalDateTime dateDebut,

                     LocalDateTime dateFin, String lieu, int

                             capaciteMax, double tarif) {
        this.id = id;

        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
        this.tarif = tarif;
        this.nbParticipants = 0;
        this.statut = StatutEvenement.PLANIFIE;
        this.inscriptions = new ArrayList<Inscription>();
    }
    public void ajouterParticipant(Inscription inscription) throws
            CapaciteDepasseeException {
        if (estComplet()) {
            throw new CapaciteDepasseeException("L'événement " +

                    titre + " est complet !");
        }
        inscriptions.add(inscription);
        nbParticipants++;
    }
    public void retirerParticipant(Inscription inscription) throws
            EvenementException {
        if (!inscriptions.remove(inscription)) {
            throw new EvenementException("Inscription non trouvée !");
        }
        nbParticipants--;
    }
    @Override
    public boolean estComplet() {
        return nbParticipants >= capaciteMax;
    }
    @Override
    public double calculerRevenu() {
        long inscriptionsValidees = inscriptions.stream()

                .filter(i -> i.getStatut() ==

                        StatutInscription.VALIDEE)
                .count();

        return inscriptionsValidees * tarif;
    }
    public double getTauxRemplissage() {
        return (nbParticipants * 100.0) / capaciteMax;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    @Override
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin =
            dateFin; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax
            = capaciteMax; }
    public int getNbParticipants() { return nbParticipants; }
    public void setNbParticipants(int nbParticipants) {
        this.nbParticipants = nbParticipants; }
    public double getTarif() { return tarif; }
    public void setTarif(double tarif) { this.tarif = tarif; }
    public StatutEvenement getStatut() { return statut; }
    public void setStatut(StatutEvenement statut) { this.statut =
            statut; }
    public List<Inscription> getInscriptions() { return
            inscriptions; }
    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions; }
    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) { this.categorie
            = categorie; }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", lieu='" + lieu + '\'' +
                ", dateDebut=" + dateDebut +
                ", nbParticipants=" + nbParticipants + "/" +

                capaciteMax +

                ", statut=" + statut +
                '}';

    }
}