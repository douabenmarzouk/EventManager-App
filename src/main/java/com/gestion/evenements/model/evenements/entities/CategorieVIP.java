package com.gestion.evenements.model.evenements.entities;
final class CategorieVIP extends CategorieSealed {
    private double prixVIP;
    private String avantages;
    public CategorieVIP(int id, String nom, String description,
                        String couleur,

                        double prix, String avantages) {

        super(id, nom, description, couleur);
        this.prixVIP = prix;
        this.avantages = avantages;
    }
    @Override
    public String getType() {
        return "VIP";
    }
    @Override
    public double getPrix() {
        return prixVIP;
    }

    public String getAvantages() { return avantages; }
    public void setAvantages(String avantages) { this.avantages =
            avantages; }
    public void setPrixVIP(double prix) { this.prixVIP = prix; }
}