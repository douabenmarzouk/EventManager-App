
package com.gestion.evenements.model.membres.enums;

public enum NiveauAcces {
    SUPER_ADMIN(3, "Super Administrateur"),
    ADMIN(2, "Administrateur"),
    MODERATEUR(1, "Modérateur");

    private final int niveau;
    private final String libelle;

    NiveauAcces(int niveau, String libelle) {
        this.niveau = niveau;
        this.libelle = libelle;
    }

    public int getNiveau() {
        return niveau;
    }
    public String getLibelle() {
        return libelle;
    }
}