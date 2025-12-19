package com.gestion.evenements.model.evenements.enums;

public enum StatutEvenement {
    PLANIFIE("Planifié"),
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    ANNULE("Annulé");
    private final String libelle;
    StatutEvenement(String libelle) {
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
