package com.gestion.evenements.model.evenements.entities;

final class CategorieGratuite extends CategorieSealed {
    public CategorieGratuite(int id, String nom, String
            description, String couleur) {
        super(id, nom, description, couleur);
    }
    @Override
    public String getType() {
        return "Gratuite";
    }
    @Override
    public double getPrix() {
        return 0.0;
    }
}