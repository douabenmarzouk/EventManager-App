
package com.gestion.evenements.model.membres.enums;

public enum StatutMembre {
    ACTIF("Actif", "Membre actif, peut s'inscrire aux événements"),
    INACTIF("Inactif", "Membre inactif, ne peut pas s'inscrire"),
    SUSPENDU("Suspendu", "Membre suspendu temporairement");

    private final String libelle;
    private final String description;

    StatutMembre(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }
}