package com.gestion.evenements.model.evenements.entities;
import java.util.Objects;
/**import javax.persistence.*;
 * @Entity
 * @Table(name = "categories")**/
public class Categorie {
    /** @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int id;
     @Column(nullable = false, unique = true, length = 100)
     private String nom;
     @Column(length = 500)
     private String description;
     @Column(length = 20)
     private String couleur;
     // Constructeur par défaut OBLIGATOIRE pour JPA
     public Categorie() {}**/
    private int id;
    private String nom;
    private String description;
    private String couleur;
    public Categorie(int id, String nom, String description, String
            couleur) {
        this.id = id;
        this.nom = nom;
        this.description = description;

        this.couleur = couleur;
    }
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur =
            couleur; }
    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", couleur='" + couleur + '\'' +
                '}';

    }
}
