package com.gestion.evenements.model.evenements.enums;

public enum StatutInscription {
    EN_ATTENTE("En attente"),
    CONFIRMEE("Confirmée"),
    ANNULEE("Annulée"),

    VALIDEE("Validée");
    private final String libelle;
    StatutInscription(String libelle) {
        this.libelle = libelle;
    }
    public String getLibelle() {
        return libelle;
    }
    @Override
    public String toString() {
        return libelle;
    }
}