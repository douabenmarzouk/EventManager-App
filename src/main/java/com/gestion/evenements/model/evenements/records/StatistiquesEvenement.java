package com.gestion.evenements.model.evenements.records;
public record StatistiquesEvenement(
        int evenementId,
        String titreEvenement,
        int nbInscrits,
        int capaciteMax,
        double tauxRemplissage,
        double revenu,
        int nbConfirmees,
        int nbEnAttente,
        int nbAnnulees
) {
    public void afficher() {
        System.out.println("\n--- Statistiques : " + titreEvenement
                + " ---");

        System.out.println("Inscrits : " + nbInscrits + "/" +
                capaciteMax);
        System.out.println("Taux de remplissage : " +
                String.format("%.1f", tauxRemplissage) + "%");
        System.out.println("Revenu : " + String.format("%.2f",
                revenu) + " DT");
        System.out.println("Inscriptions confirmées : " +
                nbConfirmees);
        System.out.println("En attente : " + nbEnAttente);
        System.out.println("Annulées : " + nbAnnulees);
    }
}
