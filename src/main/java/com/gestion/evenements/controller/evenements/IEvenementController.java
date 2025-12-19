package com.gestion.evenements.controller.evenements;
import com.gestion.evenements.model.evenements.entities.Evenement;
import
com.gestion.evenements.model.evenements.exceptions.EvenementException;
import java.util.List;
public interface IEvenementController {
    void creerEvenement(Evenement evenement) throws
            EvenementException;
    void modifierEvenement(Evenement evenement) throws
            EvenementException;
    void supprimerEvenement(int id) throws EvenementException;
    Evenement obtenirEvenement(int id);
    List<Evenement> listerEvenements();
    List<Evenement> rechercherEvenement(String critere);
}