package com.gestion.evenements.model.membres.record;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Feedback(

        int idMembre,
        int note,
        String commentaire,
        LocalDate date          // ← AJOUTÉ
) {
    // Constructeur compact avec validation
    public Feedback {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le commentaire ne peut pas être vide");
        }
    }

    // Constructeur pratique : date = aujourd'hui
    public Feedback(int idMembre, int note, String commentaire) {
        this(idMembre, note, commentaire, LocalDate.now());
    }

    public String getEtoiles() {
        return "⭐".repeat(note);
    }

    public String getDateFormatee() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void afficher() {
        System.out.println("\n=== Feedback ===");
        System.out.println("Feedback  ID : " + idMembre);
        System.out.println("Membre ID : " + idMembre);
        System.out.println("Note : " + getEtoiles() + " (" + note + "/5)");
        System.out.println("Commentaire : " + commentaire);
        System.out.println("Date : " + getDateFormatee());
    }
}