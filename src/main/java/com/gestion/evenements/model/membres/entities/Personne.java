package com.gestion.evenements.model.membres.entities;

import com.gestion.evenements.model.membres.interfaces.INotifiable;
import com.gestion.evenements.model.membres.exceptions.MembreException;
import com.gestion.evenements.model.membres.entities.Validateurs;


import java.time.LocalDate;

public abstract  sealed class Personne implements INotifiable
        permits Administrateur,Membre {
    protected int id;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String telephone;
    protected LocalDate dateInscription;
    public Personne() {
        this.dateInscription = LocalDate.now();
    }
    public Personne(int id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateInscription = LocalDate.now();
    }
    public abstract String getRole();
    @Override
    public void envoyerNotification(String message) {
        System.out.println("📧 Notification à " + getNomComplet() + " : " + message);
    }

    @Override
    public void envoyerEmail(String sujet, String corps) {
        System.out.println(" Email envoyé à : " + email);
        System.out.println("Sujet : " + sujet);
        System.out.println("Corps : " + corps);
        System.out.println("---");
    }
//interface fonctionnelle
public void valider() throws MembreException {
     Validateurs.PERSONNE.valider(this);
}



    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public void afficherInfos() {
        System.out.println("=== Informations " + getRole() + " ===");
        System.out.println("ID : " + id);
        System.out.println("Nom complet : " + getNomComplet());
        System.out.println("Email : " + email);
        System.out.println("Téléphone : " + telephone);
        System.out.println("Date d'inscription : " + dateInscription);
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }
}