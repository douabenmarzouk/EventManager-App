package com.gestion.evenements.utils;

import com.gestion.evenements.model.membres.entities.Membre;

public class UserSession {

    private static UserSession instance;
    private Membre currentMembre;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public Membre getCurrentMembre() {
        return currentMembre;
    }

    public void setCurrentMembre(Membre membre) {
        this.currentMembre = membre;
    }

    public void clear() {
        this.currentMembre = null;
    }
}
