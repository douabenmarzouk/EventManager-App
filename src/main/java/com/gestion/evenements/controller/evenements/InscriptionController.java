package com.gestion.evenements.controller.evenements;

import com.gestion.evenements.model.evenements.entities.Inscription;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.enums.StatutInscription;
import com.gestion.evenements.model.evenements.exceptions.EvenementException;
import com.gestion.evenements.model.evenements.exceptions.CapaciteDepasseeException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class InscriptionController {
    private List<Inscription> inscriptions;
    private EvenementController evenementController;
    private int prochainId;
    public InscriptionController() {
        this.inscriptions = new ArrayList<>();
        this.evenementController = new EvenementController();
        this.prochainId = 1;
        initialiserDonneesTest();
    }
    /**
     * Initialise quelques inscriptions de test
     */
    private void initialiserDonneesTest() {
        try {
// Inscription 1
            Inscription insc1 = new Inscription(prochainId++, 101,

                    1);

            insc1.setStatut(StatutInscription.CONFIRMEE);
            insc1.setMontantPaye(25.00);
            insc1.setConfirmed(true);
            inscriptions.add(insc1);
// Inscription 2

            Inscription insc2 = new Inscription(prochainId++, 102,

                    1);

            insc2.setStatut(StatutInscription.EN_ATTENTE);
            inscriptions.add(insc2);
// Inscription 3
            Inscription insc3 = new Inscription(prochainId++, 103,

                    2);

            insc3.setStatut(StatutInscription.VALIDEE);
            insc3.setMontantPaye(50.00);
            insc3.setConfirmed(true);
            inscriptions.add(insc3);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des inscriptions: " + e.getMessage());
        }
    }
    /**
     * Inscrit un membre à un événement
     * @param membreId L'identifiant du membre
     * @param evenementId L'identifiant de l'événement
     * @throws EvenementException Si une erreur se produit
     */
    public void inscrireMembre(int membreId, int evenementId)
            throws EvenementException {
// Vérifier que l'événement existe
        Evenement evenement =
                evenementController.obtenirEvenement(evenementId);
        if (evenement == null) {
            throw new EvenementException("Événement non trouvé avec l'ID: " + evenementId);
        }
// Vérifier que le membre n'est pas déjà inscrit
        boolean dejaInscrit = inscriptions.stream()

                .anyMatch(i -> i.getMembreId() == membreId &&
                        i.getEvenementId() == evenementId &&
                        i.getStatut() != StatutInscription.ANNULEE);

        if (dejaInscrit) {
            throw new EvenementException("Le membre est déjà inscrit à cet événement");
        }
// Vérifier que l'événement n'est pas complet
        if (evenement.estComplet()) {

            throw new CapaciteDepasseeException("L'événement est complet");
        }
// Créer l'inscription
        Inscription inscription = new Inscription(prochainId++,
                membreId, evenementId);
        inscriptions.add(inscription);
// Ajouter le participant à l'événement
        try {
            evenement.ajouterParticipant(inscription);
        } catch (CapaciteDepasseeException e) {
// Si l'ajout échoue, retirer l'inscription qu'on vientde créer

            inscriptions.remove(inscription);
            throw e;
        }
    }
    /**
     * Valide une inscription (change le statut à VALIDEE)
     * @param inscriptionId L'identifiant de l'inscription
     * @throws EvenementException Si l'inscription n'existe pas
     */
    public void validerInscription(int inscriptionId) throws
            EvenementException {
        Inscription inscription = rechercherParId(inscriptionId);
        if (inscription == null) {
            throw new EvenementException("Inscription non trouvée avec l'ID: " + inscriptionId);
        }
        if (inscription.getStatut() == StatutInscription.ANNULEE) {
            throw new EvenementException("Impossible de valider une inscription annulée");
        }
        inscription.setStatut(StatutInscription.VALIDEE);
        System.out.println("Inscription #" + inscriptionId + "validée avec succès");
    }
    /**
     * Annule une inscription
     * @param inscriptionId L'identifiant de l'inscription
     * @throws EvenementException Si l'inscription n'existe pas
     */

    public void annulerInscription(int inscriptionId) throws
            EvenementException {
        Inscription inscription = rechercherParId(inscriptionId);
        if (inscription == null) {
            throw new EvenementException("Inscription non trouvée avec l'ID: " + inscriptionId);
        }
// Récupérer l'événement pour retirer le participant
        Evenement evenement =
                evenementController.obtenirEvenement(inscription.getEvenementId())
                ;
        if (evenement != null) {
            try {
                evenement.retirerParticipant(inscription);
            } catch (EvenementException e) {
                System.err.println("Avertissement: " +

                        e.getMessage());
            }
        }
// Annuler l'inscription
        inscription.annuler();
        System.out.println("Inscription #" + inscriptionId + "+annulée avec succès");
    }
    /**
     * Liste toutes les inscriptions
     * @return La liste de toutes les inscriptions
     */
    public List<Inscription> listerInscriptions() {
        return new ArrayList<>(inscriptions);
    }
    /**
     * Modifie une inscription existante
     * @param inscription L'inscription avec les modifications
     * @throws EvenementException Si l'inscription n'existe pas
     */
    public void modifierInscription(Inscription inscription) throws
            EvenementException {
        if (inscription == null) {
            throw new EvenementException("L'inscription ne peut pas être null");
        }

        Inscription existante =
                rechercherParId(inscription.getId());
        if (existante == null) {
            throw new EvenementException("Inscription non trouvée avec l'ID: " + inscription.getId());
        }
        int index = inscriptions.indexOf(existante);
        inscriptions.set(index, inscription);
    }
    /**
     * Confirme une inscription (le membre confirme sa présence)
     * @param inscriptionId L'identifiant de l'inscription
     * @throws EvenementException Si l'inscription n'existe pas
     */
    public void confirmerInscription(int inscriptionId) throws
            EvenementException {
        Inscription inscription = rechercherParId(inscriptionId);
        if (inscription == null) {
            throw new EvenementException("Inscription non trouvée");
        }
        inscription.confirmer();
    }
    /**
     * Obtient une inscription par son ID
     * @param id L'identifiant de l'inscription
     * @return L'inscription trouvée ou null
     */
    public Inscription obtenirInscription(int id) {
        return rechercherParId(id);
    }
    /**
     * Obtient les inscriptions d'un membre
     * @param membreId L'identifiant du membre
     * @return La liste des inscriptions du membre
     */
    public List<Inscription> obtenirInscriptionsMembre(int
                                                               membreId) {
        return inscriptions.stream()

                .filter(i -> i.getMembreId() == membreId)
                .collect(Collectors.toList());

    }

    /**
     * Obtient les inscriptions d'un événement
     * @param evenementId L'identifiant de l'événement
     * @return La liste des inscriptions de l'événement
     */
    public List<Inscription> obtenirInscriptionsEvenement(int
                                                                  evenementId) {
        return inscriptions.stream()

                .filter(i -> i.getEvenementId() == evenementId)
                .collect(Collectors.toList());

    }
    /**
     * Filtre les inscriptions par statut
     * @param statut Le statut à filtrer
     * @return La liste des inscriptions avec ce statut
     */
    public List<Inscription> filtrerParStatut(StatutInscription
                                                      statut) {
        return inscriptions.stream()

                .filter(i -> i.getStatut() == statut)
                .collect(Collectors.toList());

    }
    /**
     * Obtient les inscriptions en attente de validation
     * @return La liste des inscriptions en attente
     */
    public List<Inscription> obtenirInscriptionsEnAttente() {
        return filtrerParStatut(StatutInscription.EN_ATTENTE);
    }
    /**
     * Obtient les inscriptions validées
     * @return La liste des inscriptions validées
     */
    public List<Inscription> obtenirInscriptionsValidees() {
        return filtrerParStatut(StatutInscription.VALIDEE);
    }
    /**
     * Obtient les inscriptions confirmées
     * @return La liste des inscriptions confirmées
     */
    public List<Inscription> obtenirInscriptionsConfirmees() {
        return filtrerParStatut(StatutInscription.CONFIRMEE);
    }

    /**
     * Obtient les inscriptions annulées
     * @return La liste des inscriptions annulées
     */
    public List<Inscription> obtenirInscriptionsAnnulees() {
        return filtrerParStatut(StatutInscription.ANNULEE);
    }
    /**
     * Compte le nombre d'inscriptions pour un événement
     * @param evenementId L'identifiant de l'événement
     * @return Le nombre d'inscriptions (hors annulées)
     */
    public int compterInscriptionsEvenement(int evenementId) {
        return (int) inscriptions.stream()

                .filter(i -> i.getEvenementId() == evenementId)
                .filter(i -> i.getStatut() !=

                        StatutInscription.ANNULEE)
                .count();

    }
    /**
     * Calcule le revenu d'un événement (inscriptions validées)
     * @param evenementId L'identifiant de l'événement
     * @return Le revenu total
     */
    public double calculerRevenuEvenement(int evenementId) {
        return inscriptions.stream()

                .filter(i -> i.getEvenementId() == evenementId)
                .filter(i -> i.getStatut() ==

                        StatutInscription.VALIDEE)

                .mapToDouble(Inscription::getMontantPaye)
                .sum();

    }
    /**
     * Vérifie si un membre est inscrit à un événement
     * @param membreId L'identifiant du membre
     * @param evenementId L'identifiant de l'événement
     * @return true si le membre est inscrit, false sinon
     */
    public boolean estInscrit(int membreId, int evenementId) {
        return inscriptions.stream()

                .anyMatch(i -> i.getMembreId() == membreId &&
                        i.getEvenementId() == evenementId &&
                        i.getStatut() != StatutInscription.ANNULEE);

    }

    /**
     * Recherche une inscription par son ID
     * @param id L'identifiant de l'inscription
     * @return L'inscription trouvée ou null
     */
    private Inscription rechercherParId(int id) {
        return inscriptions.stream()

                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);

    }
    /**
     * Obtient le nombre total d'inscriptions (hors annulées)
     * @return Le nombre d'inscriptions actives
     */
    public int getNombreInscriptionsActives() {
        return (int) inscriptions.stream()
                .filter(i -> i.getStatut() !=

                        StatutInscription.ANNULEE)
                .count();

    }
    /**
     * Obtient le nombre total d'inscriptions
     * @return Le nombre total d'inscriptions
     */
    public int getNombreTotalInscriptions() {
        return inscriptions.size();
    }
    /**
     * Supprime une inscription
     * @param inscriptionId L'identifiant de l'inscription à
    supprimer
     * @throws EvenementException Si l'inscription n'existe pas
     */
    public void supprimerInscription(int inscriptionId) throws
            EvenementException {
        Inscription inscription = rechercherParId(inscriptionId);
        if (inscription == null) {
            throw new EvenementException("Inscription non trouvée avec l'ID: " + inscriptionId);
        }
        inscriptions.remove(inscription);
        System.out.println("Inscription #" + inscriptionId + " supprimée avec succès");

    }
}