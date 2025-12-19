package com.gestion.evenements.controller.evenements;

import com.gestion.evenements.controller.evenements.InscriptionController;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.entities.Inscription;
import com.gestion.evenements.model.evenements.enums.StatutInscription;
import com.gestion.evenements.model.evenements.records.Rapport;
import com.gestion.evenements.model.evenements.records.StatistiquesEvenement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static javax.swing.text.html.HTML.Tag.DT;

/**
 * Contrôleur pour la génération de rapports et statistiques
 */
public class RapportController {
    private EvenementController evenementController;
    private InscriptionController inscriptionController;
    public RapportController() {
        this.evenementController = new EvenementController();
        this.inscriptionController = new InscriptionController();
    }
    /**
     * Génère un rapport global sur tous les événements
     */
    public Rapport genererRapportGlobal() {
        List<Evenement> evenements =
                evenementController.listerEvenements();
        int nbEvenements = evenements.size();
        int nbParticipants =
                evenementController.calculerNombreTotalParticipants();
        double revenuTotal =
                evenementController.calculerRevenuTotal();
        StringBuilder contenu = new StringBuilder();

        contenu.append("📊 VUE D'ENSEMBLE\n\n");
// Statistiques par statut
        long nbPlanifies = evenements.stream()
                .filter(e -> e.getStatut() ==

                                com.gestion.evenements.model.evenements.enums.StatutEvenement.PLANIFIE)

                .count();

        long nbEnCours = evenements.stream()
                .filter(e -> e.getStatut() ==

                                com.gestion.evenements.model.evenements.enums.StatutEvenement.EN_COURS)

                .count();

        long nbTermines = evenements.stream()
                .filter(e -> e.getStatut() ==

                                com.gestion.evenements.model.evenements.enums.StatutEvenement.TERMINE)

                .count();

        long nbAnnules = evenements.stream()
                .filter(e -> e.getStatut() ==

                                com.gestion.evenements.model.evenements.enums.StatutEvenement.ANNULE)

                .count();

        contenu.append("Événements par statut:\n");
        contenu.append(" • Planifiés :").append(nbPlanifies).append("\n");
                contenu.append(" • En cours :").append(nbEnCours).append("\n");
                        contenu.append(" • Terminés :").append(nbTermines).append("\n");
                                contenu.append(" • Annulés :").append(nbAnnules).append("\n\n");
// Taux de remplissage moyen
        double tauxMoyen = evenements.stream()

                .mapToDouble(Evenement::getTauxRemplissage)
                .average()
                .orElse(0.0);

        contenu.append("Taux de remplissage moyen : ")
                .append(String.format("%.1f%%", tauxMoyen))
                .append("\n\n");

// Top 5 des événements par participants
        contenu.append("📈 TOP 5 - Événements les plus populaires:\n\n");
        evenements.stream()

                .sorted((e1, e2) ->

                        Integer.compare(e2.getNbParticipants(), e1.getNbParticipants()))

                .limit(5)
                .forEach(evt -> {
                    contenu.append(" ").append(evt.getTitre())

                            .append(" -").append(evt.getNbParticipants())

                                            .append(" participants (")
                                            .append(String.format("%.1f%%",

                                                    evt.getTauxRemplissage()))

                                            .append(")\n");

                });

        contenu.append("\n💰 TOP 5 - Événements les plus rentables:\n\n");
        evenements.stream()

                .sorted((e1, e2) ->

                        Double.compare(e2.calculerRevenu(), e1.calculerRevenu()))

                .limit(5)
                .forEach(evt -> {
                    contenu.append(" ").append(evt.getTitre())

                            .append(" -").append(String.format("%.2f DT", evt.calculerRevenu()))

                                            .append("\n");

                });

        return new Rapport(

                LocalDateTime.now(),
                "RAPPORT GLOBAL DES ÉVÉNEMENTS",
                nbEvenements,
                nbParticipants,
                revenuTotal,
                contenu.toString()

        );
    }
    /**
     * Génère un rapport détaillé pour un événement spécifique
     */
    public Rapport genererRapportEvenement(int evenementId) {
        Evenement evenement =
                evenementController.obtenirEvenement(evenementId);
        if (evenement == null) {
            return null;
        }
        List<Inscription> inscriptions =
                inscriptionController.obtenirInscriptionsEvenement(evenementId);

        StringBuilder contenu = new StringBuilder();
// Informations générales
        contenu.append("📅 INFORMATIONS GÉNÉRALES\n\n");
        contenu.append("Titre :").append(evenement.getTitre()).append("\n");
                contenu.append("Description :").append(evenement.getDescription()).append("\n");
                        contenu.append("Lieu :").append(evenement.getLieu()).append("\n");
                                DateTimeFormatter formatter =
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        contenu.append("Date de début :").append(evenement.getDateDebut().format(formatter)).append("\n")
        ;
        contenu.append("Date de fin :").append(evenement.getDateFin().format(formatter)).append("\n");
                contenu.append("Statut :").append(evenement.getStatut()).append("\n\n");
// Statistiques des inscriptions
                        contenu.append("📊 STATISTIQUES DES INSCRIPTIONS\n\n");
        contenu.append("Capacité maximale :").append(evenement.getCapaciteMax()).append("\n");
                contenu.append("Participants inscrits :").append(evenement.getNbParticipants()).append("\n");
                        contenu.append("Taux de remplissage : ")
                                .append(String.format("%.1f%%",

                                        evenement.getTauxRemplissage()))
                                .append("\n");

        contenu.append("Places disponibles : ")
                .append(evenement.getCapaciteMax() -

                        evenement.getNbParticipants())
                .append("\n\n");
// Répartition par statut
        long nbEnAttente = inscriptions.stream()
                .filter(i -> i.getStatut() ==

                        StatutInscription.EN_ATTENTE)
                .count();

        long nbConfirmees = inscriptions.stream()
                .filter(i -> i.getStatut() ==

                        StatutInscription.CONFIRMEE)
                .count();

        long nbValidees = inscriptions.stream()

                .filter(i -> i.getStatut() ==

                        StatutInscription.VALIDEE)
                .count();

        long nbAnnulees = inscriptions.stream()
                .filter(i -> i.getStatut() ==

                        StatutInscription.ANNULEE)
                .count();

        contenu.append("Répartition des inscriptions:\n");
        contenu.append(" • En attente :").append(nbEnAttente).append("\n");
                contenu.append(" • Confirmées :").append(nbConfirmees).append("\n");
                        contenu.append(" • Validées :").append(nbValidees).append("\n");
                                contenu.append(" • Annulées :").append(nbAnnulees).append("\n\n");
// Informations financières
                                        contenu.append("💰 INFORMATIONS FINANCIÈRES\n\n");
        contenu.append("Tarif unitaire :").append(String.format("%.2f DT",
                evenement.getTarif())).append("\n");
        contenu.append("Revenu généré :").append(String.format("%.2f DT",
                evenement.calculerRevenu())).append("\n");
        contenu.append("Revenu potentiel maximum : ")
                .append(String.format("%.2f DT",
                        evenement.getCapaciteMax() * evenement.getTarif()))

                .append("\n");

        double pourcentageRevenu = evenement.getCapaciteMax() > 0 ?

                (evenement.calculerRevenu() /

                        (evenement.getCapaciteMax() * evenement.getTarif())) * 100 : 0;
        contenu.append("Pourcentage du revenu potentiel : ")
                .append(String.format("%.1f%%", pourcentageRevenu))
                .append("\n");
        return new Rapport(

                LocalDateTime.now(),
                "RAPPORT - " + evenement.getTitre(),
                1,
                evenement.getNbParticipants(),
                evenement.calculerRevenu(),
                contenu.toString()

        );
    }

    /**
     * Calcule les statistiques détaillées d'un événement
     */
    public StatistiquesEvenement calculerStatistiquesEvenement(int
                                                                       evenementId) {
        Evenement evenement =
                evenementController.obtenirEvenement(evenementId);
        if (evenement == null) {
            return null;
        }
        List<Inscription> inscriptions =
                inscriptionController.obtenirInscriptionsEvenement(evenementId);
        int nbConfirmees = (int) inscriptions.stream()

                .filter(i -> i.getStatut() ==

                        StatutInscription.CONFIRMEE)
                .count();

        int nbEnAttente = (int) inscriptions.stream()

                .filter(i -> i.getStatut() ==

                        StatutInscription.EN_ATTENTE)
                .count();

        int nbAnnulees = (int) inscriptions.stream()
                .filter(i -> i.getStatut() ==

                        StatutInscription.ANNULEE)
                .count();

        return new StatistiquesEvenement(
                evenement.getId(),
                evenement.getTitre(),
                evenement.getNbParticipants(),
                evenement.getCapaciteMax(),
                evenement.getTauxRemplissage(),
                evenement.calculerRevenu(),
                nbConfirmees,
                nbEnAttente,
                nbAnnulees

        );
    }
    /**
     * Génère un rapport des inscriptions en attente
     */
    public Rapport genererRapportInscriptionsEnAttente() {
        List<Inscription> inscriptionsEnAttente =
                inscriptionController.obtenirInscriptionsEnAttente();

        StringBuilder contenu = new StringBuilder();
        contenu.append("📋 LISTE DES INSCRIPTIONS EN ATTENTE DE VALIDATION\n\n");
        if (inscriptionsEnAttente.isEmpty()) {
            contenu.append("Aucune inscription en attente.\n");
        } else {
            for (Inscription inscription : inscriptionsEnAttente) {
                Evenement evenement =

                        evenementController.obtenirEvenement(inscription.getEvenementId())
                        ;

                contenu.append("ID Inscription :").append(inscription.getId()).append("\n");
                        contenu.append(" • Membre ID :").append(inscription.getMembreId()).append("\n");

                                contenu.append(" • Événement : ").append(evenement

                                        != null ? evenement.getTitre() : "N/A").append("\n");
                contenu.append(" • Date d'inscription : ")

                        .append(inscription.getDateInscription().format(DateTimeFormatter.
                                ofPattern("dd/MM/yyyy HH:mm")))
                        .append("\n");
                contenu.append(" • Montant :").append(String.format("%.2f DT", inscription.getMontantPaye())).append("\n\n");

            }
        }
        return new Rapport(

                LocalDateTime.now(),
                "INSCRIPTIONS EN ATTENTE",
                0,
                inscriptionsEnAttente.size(),
                0.0,
                contenu.toString()

        );
    }
}