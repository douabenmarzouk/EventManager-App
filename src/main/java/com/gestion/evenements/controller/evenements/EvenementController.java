
package com.gestion.evenements.controller.evenements;
import com.gestion.evenements.model.evenements.entities.Evenement;
import com.gestion.evenements.model.evenements.exceptions.EvenementException;
import com.gestion.evenements.model.evenements.interfaces.IRecherchable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/**
 * Contrôleur pour la gestion des événements
 */
public class EvenementController implements IEvenementController, IRecherchable<Evenement> {
    private List<Evenement> evenements;
    private int prochainId;
    public EvenementController() {
        this.evenements = new ArrayList<>();
        this.prochainId = 1;
        initialiserDonneesTest();
    }
    /**
     * Initialise quelques événements de test
     */
    private void initialiserDonneesTest() {
        try {
            Evenement evt1 = new Evenement(

                    prochainId++,
                    "Concert de Jazz",
                    "Soirée jazz avec des artistes locaux",
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(10).plusHours(3),
                    "Théâtre Municipal",
                    150,
                    25.00

            );
            Evenement evt2 = new Evenement(

                    prochainId++,
                    "Conférence Tech 2025",
                    "Conférence sur les nouvelles technologies",
                    LocalDateTime.now().plusDays(15),
                    LocalDateTime.now().plusDays(15).plusHours(6),
                    "Centre de Conventions",
                    300,
                    50.00

            );
            Evenement evt3 = new Evenement(

                    prochainId++,
                    "Marathon de Tunis",
                    "Course annuelle de 42km",
                    LocalDateTime.now().plusDays(30),
                    LocalDateTime.now().plusDays(30).plusHours(4),
                    "Avenue Habib Bourguiba",
                    500,
                    30.00

            );
            evenements.add(evt1);
            evenements.add(evt2);
            evenements.add(evt3);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: "

                    + e.getMessage());
        }
    }
    @Override
    public void creerEvenement(Evenement evenement) throws
            EvenementException {
        if (evenement == null) {
            throw new EvenementException("L'événement ne peut pas être null");
        }
        if (evenement.getTitre() == null ||
                evenement.getTitre().trim().isEmpty()) {

            throw new EvenementException("Le titre est obligatoire");
        }
        if (evenement.getId() == 0) {
            evenement.setId(prochainId++);
        }

        evenements.add(evenement);
    }
    @Override
    public void modifierEvenement(Evenement evenement) throws
            EvenementException {
        if (evenement == null) {
            throw new EvenementException("L'événement ne peut pas être null");
        }
        Evenement existant = rechercherParId(evenement.getId());
        if (existant == null) {
            throw new EvenementException("Événement non trouvé");
        }
        int index = evenements.indexOf(existant);
        evenements.set(index, evenement);
    }
    @Override
    public void supprimerEvenement(int id) throws
            EvenementException {
        Evenement evenement = rechercherParId(id);
        if (evenement == null) {
            throw new EvenementException("Événement non trouvé");
        }
        evenements.remove(evenement);
    }
    @Override
    public Evenement obtenirEvenement(int id) {
        return rechercherParId(id);
    }
    @Override
    public List<Evenement> listerEvenements() {
        return new ArrayList<>(evenements);
    }
    @Override
    public List<Evenement> rechercherEvenement(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            return listerEvenements();
        }

        String critereLower = critere.toLowerCase().trim();
        return evenements.stream()
                .filter(evt ->

                        evt.getTitre().toLowerCase().contains(critereLower) ||

                                (evt.getDescription() != null &&
                                        evt.getDescription().toLowerCase().contains(critereLower)) ||
                                evt.getLieu().toLowerCase().contains(critereLower)

                )
                .collect(Collectors.toList());

    }
    @Override
    public List<Evenement> rechercher(String critere) {
        return rechercherEvenement(critere);
    }
    @Override
    public List<Evenement> filtrer(Predicate<Evenement> critere) {
        if (critere == null) {
            return listerEvenements();
        }
        return evenements.stream()
                .filter(critere)
                .collect(Collectors.toList());

    }
    /**
     * Calcule le revenu total de tous les événements
     */
    public double calculerRevenuTotal() {
        return evenements.stream()

                .mapToDouble(Evenement::calculerRevenu)
                .sum();

    }
    /**
     * Calcule le nombre total de participants
     */
    public int calculerNombreTotalParticipants() {
        return evenements.stream()

                .mapToInt(Evenement::getNbParticipants)
                .sum();

    }

    /**
     * Recherche un événement par son ID
     */
    private Evenement rechercherParId(int id) {
        return evenements.stream()

                .filter(evt -> evt.getId() == id)
                .findFirst()
                .orElse(null);

    }
    /**
     * Vérifie si un événement existe
     */
    public boolean evenementExiste(int id) {
        return rechercherParId(id) != null;
    }
    /**
     * Obtient le nombre total d'événements
     */
    public int getNombreEvenements() {
        return evenements.size();
    }
}