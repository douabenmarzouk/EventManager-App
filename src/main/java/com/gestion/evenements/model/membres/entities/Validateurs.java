package com.gestion.evenements.model.membres.entities;

import com.gestion.evenements.model.membres.interfaces.IValidateur;
import com.gestion.evenements.model.membres.exceptions.MembreException;

public class Validateurs {

    public static final IValidateur<String> EMAIL = (email) -> {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new MembreException("Email invalide", "EMAIL_INVALIDE");
        }
        return true;
    };

    public static final IValidateur<String> NOM = (nom) -> {
        if (nom == null || nom.trim().isEmpty()) {
            throw new MembreException("Le nom est obligatoire", "NOM_OBLIGATOIRE");
        }
        return true;
    };

    public static final IValidateur<String> LOCALISATION = (localisation) -> {
        if (localisation == null || localisation.trim().isEmpty()) {
            throw new MembreException("La localisation est obligatoire", "LOCALISATION_OBLIGATOIRE");
        }
        return true;
    };

    public static final IValidateur<Double> BUDGET = (budget) -> {
        if (budget == null || budget < 0) {
            throw new MembreException("Le budget ne doit pas être nul ou négatif", "BUDGET_INVALIDE");
        }
        return true;
    };

    public static final IValidateur<String> PRENOM = (prenom) -> {
        if (prenom == null || prenom.trim().isEmpty()) {
            throw new MembreException("Le prénom est obligatoire", "PRENOM_OBLIGATOIRE");
        }
        return true;
    };

    public static final IValidateur<String> TELEPHONE = (tel) -> {
        if (tel == null || tel.length() < 8) {
            throw new MembreException("Numéro de téléphone invalide", "TELEPHONE_INVALIDE");
        }
        return true;
    };

    public static final IValidateur<Personne> PERSONNE = (p) -> {
        NOM.valider(p.getNom());
        PRENOM.valider(p.getPrenom());
        EMAIL.valider(p.getEmail());
        TELEPHONE.valider(p.getTelephone());
        return true;
    };

    public static final IValidateur<Association> ASSOCIATION = (a) -> {
        NOM.valider(a.getNom());
        EMAIL.valider(a.getEmail());
        LOCALISATION.valider(a.getLocalisation());
        BUDGET.valider(a.getBudget());
        return true;
    };
}
