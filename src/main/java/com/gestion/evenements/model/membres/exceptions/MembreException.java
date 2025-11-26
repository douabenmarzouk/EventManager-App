package com.gestion.evenements.model.membres.exceptions;

public class MembreException extends Exception {
    //
    // 1️⃣ Attributs de l'exception
    private final String codErreur;      // Code unique (ex: ERR_001)
    private final long timestamp;        // Quand c'est arrivé

    // 2️⃣ Constructeur
    public MembreException(String message, String codErreur) {
        super(message);                  // Envoie message à Exception
        this.codErreur = codErreur;
        this.timestamp = System.currentTimeMillis();
    }

    // 3️⃣ Getters pour récupérer info
    public String getCodErreur() {
        return codErreur;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
