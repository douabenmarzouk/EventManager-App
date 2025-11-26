package com.gestion.evenements.model.membres.record;

import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.exceptions.MembreException;
import java.time.LocalDate;

/**
 * Record Feedback
 * Immuable et validé avant création
 */
public record Feedback(Association association, String message, LocalDate date) {

    /**
     * Méthode de création validée
     * Permet de lever MembreException avant de créer le record
     */
    public static Feedback creerFeedback(Association association, String message, LocalDate date) throws MembreException {
        if (association == null) {
            throw new MembreException("Association non valide", "ASSOCIATION_NULL");
        }
        if (message == null || message.isBlank()) {
            throw new MembreException("Message vide", "MESSAGE_VIDE");
        }
        if (date == null) {
            throw new MembreException("Date manquante", "DATE_MANQUANTE");
        }
        return new Feedback(association, message, date);
    }
}
