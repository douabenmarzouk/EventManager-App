package com.gestion.evenements.model.evenements.entities;

final class CategorieStandard extends CategorieSealed {

    private double prixStandard;
    public CategorieStandard(int id, String nom, String
            description, String couleur, double prix) {
        super(id, nom, description, couleur);
        this.prixStandard = prix;
    }
    @Override
    public String getType() {
        return "Standard";
    }
    @Override
    public double getPrix() {
        return prixStandard;
    }
    public void setPrixStandard(double prix) {
        this.prixStandard = prix;
    }
}
