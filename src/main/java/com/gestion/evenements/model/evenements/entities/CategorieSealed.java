package com.gestion.evenements.model.evenements.entities;

public sealed abstract class CategorieSealed

        permits CategorieGratuite, CategorieStandard, CategorieVIP
{
    private int id;
    private String nom;
    private String description;
    private String couleur;
    protected CategorieSealed(int id, String nom, String
            description, String couleur) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.couleur = couleur;
    }
    // Méthode abstraite pour définir le comportement spécifique
    public abstract String getType();
    // Méthode abstraite pour obtenir le prix (si applicable)
    public abstract double getPrix();
    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public String getCouleur() { return couleur; }
    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) {
        this.description = description; }
    public void setCouleur(String couleur) { this.couleur =
            couleur; }
    @Override
    public String toString() {
        return getType() + " - " + nom + " (" + couleur + ")";}}
