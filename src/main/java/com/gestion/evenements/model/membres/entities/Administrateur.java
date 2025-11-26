package com.gestion.evenements.model.membres.entities;

import com.gestion.evenements.model.membres.enums.NiveauAcces;
import com.gestion.evenements.model.membres.exceptions.MembreException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Administrateur extends Personne {

    private NiveauAcces niveau;
    private LocalDate dateNomination;

    private List<Membre> membres = new ArrayList<>();

    // Constructeur par défaut
    public Administrateur() {
        super();
        this.niveau = NiveauAcces.MODERATEUR;
        this.dateNomination = LocalDate.now();
    }

    // Constructeur complet
    public Administrateur(int id, String nom, String prenom, String email, String telephone, NiveauAcces niveau) {
        super(id, nom, prenom, email, telephone);
        this.niveau = niveau;
        this.dateNomination = LocalDate.now();
    }

    @Override
    public String getRole() {
        return "ADMINISTRATEUR";
    }

    public void genererRapport() {
        System.out.println("🔐 " + getNomComplet() + " génère un rapport...");
        System.out.println("Niveau d'accès : " + niveau.getLibelle());
        System.out.println("Rapport généré avec succès !");
    }

    public void validerInscription(String nomMembre) {
        if (niveau == NiveauAcces.MODERATEUR) {
            System.out.println("⚠️ Niveau insuffisant pour valider des inscriptions");
            return;
        }
        System.out.println(getNomComplet() + " a validé l'inscription de " + nomMembre);
    }

    public Membre inscrireNouveauMembre(String nom, String prenom, String email, String telephone)
            throws MembreException {

        // Vérifier que l'email n'existe pas déjà
        if (membres.stream().anyMatch(m -> m.getEmail().equals(email))) {
            throw new MembreException(
                    "Un membre avec cet email existe déjà",
                    "EMAIL_DUPLIQUE"
            );
        }

        // Créer un nouveau membre

        int nouvelId = membres.isEmpty() ? 1 : membres.get(membres.size() - 1).getId() + 1;
        Membre nouveauMembre = new Membre(nouvelId, nom, prenom, email, telephone);

        // Ajouter le membre à la liste
        membres.add(nouveauMembre);

        System.out.println("✅ Nouveau membre inscrit : " + nouveauMembre.getNomComplet());
        System.out.println("   Numéro de membre : " + nouveauMembre.getNumeroMembre());
        System.out.println("   Email : " + email);

        return nouveauMembre;
    }


    public boolean peutModifierSysteme() {
        return niveau == NiveauAcces.SUPER_ADMIN || niveau == NiveauAcces.ADMIN;
    }

    @Override
    public void afficherInfos() {
        super.afficherInfos();
        System.out.println("Niveau d'accès : " + niveau.getLibelle() + " (Niveau " + niveau.getNiveau() + ")");
        System.out.println("Date de nomination : " + dateNomination);
        System.out.println("Peut modifier le système : " + (peutModifierSysteme() ? "Oui" : "Non"));
    }

    // Getters / Setters
    public NiveauAcces getNiveau() {
        return niveau;
    }
    private Membre trouverMembre(int idMembre) throws MembreException {

        // Vérifier si le membre existe
        if (!membres.stream().anyMatch(m -> m.getId() == idMembre)) {
            throw new MembreException(
                    "Aucun membre trouvé avec l'ID : " + idMembre,
                    "MEMBRE_NON_TROUVE"
            );
        }
        // Retourner le membre trouvé
        for (Membre m : membres) {
            if (m.getId() == idMembre) {
                return m;
            }
        }
        // Cette ligne ne devrait jamais être atteinte
        throw new MembreException(
                "Aucun membre trouvé avec l'ID : " + idMembre,
                "MEMBRE_NON_TROUVE"
        );
    }

    public void validerInscriptionMembre(int idMembre) throws MembreException {
        Membre membre = trouverMembre(idMembre);

        if (membre.getStatut().getLibelle().equals("ACTIF")) {
            throw new MembreException(
                    "Ce membre est déjà actif",
                    "MEMBRE_DEJA_ACTIF"
            );
        }

        System.out.println("✅ Inscription du membre " + membre.getNomComplet() + " validée par l'association");
    }

    public void setNiveau(NiveauAcces niveau) {
        this.niveau = niveau;
    }

    public LocalDate getDateNomination() {
        return dateNomination;
    }

    public void setDateNomination(LocalDate dateNomination) {
        this.dateNomination = dateNomination;
    }
}
