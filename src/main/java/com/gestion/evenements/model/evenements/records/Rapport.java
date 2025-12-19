package com.gestion.evenements.model.evenements.records;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Rapport(
        LocalDateTime dateGeneration,
        String titre,
        int nbEvenements,
        int nbParticipants,
        double revenuTotal,
        String contenu
) {
    public void afficher() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("=".repeat(60));
        System.out.println("RAPPORT : " + titre);
        System.out.println("Généré le : " +
                dateGeneration.format(formatter));
        System.out.println("=".repeat(60));
        System.out.println("Nombre d'événements : " +
                nbEvenements);
        System.out.println("Nombre de participants : " +
                nbParticipants);
        System.out.println("Revenu total : " + String.format("%.2f",
                revenuTotal) + " DT");
        System.out.println("-".repeat(60));
        System.out.println(contenu);
        System.out.println("=".repeat(60));
    }
}