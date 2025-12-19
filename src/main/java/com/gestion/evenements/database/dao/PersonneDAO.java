package com.gestion.evenements.database.dao;

import com.gestion.evenements.database.DatabaseManager;
import com.gestion.evenements.model.membres.entities.Administrateur;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.enums.NiveauAcces;
import com.gestion.evenements.model.membres.enums.StatutMembre;

import java.sql.*;
import java.time.LocalDate;

public class PersonneDAO {

    private final Connection connection;

    public PersonneDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    // =================== AUTHENTIFICATION ===================

    /**
     * Vérifier le mot de passe d'une personne
     */
    public boolean verifierMotDePasse(String email, String motDePasse, String role) throws SQLException {
        String query = "SELECT mot_de_passe FROM personnes WHERE LOWER(email) = LOWER(?) AND role = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email.trim());
            stmt.setString(2, role.trim());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String mdpBD = rs.getString("mot_de_passe");
                System.out.println("MDP BD : '" + mdpBD + "' | Formulaire : '" + motDePasse + "'");
                return mdpBD != null && mdpBD.trim().equals(motDePasse.trim());
            } else {
                System.out.println("Aucun résultat pour email=" + email + " et role=" + role);
            }
        }

        return false;
    }


    // =================== RÉCUPÉRATION MEMBRE ===================

    /**
     * Récupérer un membre par email
     */
    public Membre getMembreParEmail(String email) throws SQLException {
        String query = """
            SELECT p.id, p.nom, p.prenom, p.email, p.telephone,
                   m.numero_membre, m.statut, m.cotisation_payee, 
                   m.montant_cotisation, m.date_paiement_cotisation
            FROM personnes p
            INNER JOIN membres m ON p.id = m.personne_id
            WHERE p.email = ? AND p.role = 'MEMBRE'
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Membre m = new Membre(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getBoolean("cotisation_payee")
                );

                m.setNumeroMembre(rs.getString("numero_membre"));
                m.setStatut(StatutMembre.valueOf(rs.getString("statut")));
                m.setMontantCotisation(rs.getDouble("montant_cotisation"));

                Date datePaiement = rs.getDate("date_paiement_cotisation");
                if (datePaiement != null) {
                    m.setDatePaiementCotisation(datePaiement.toLocalDate());
                }

                return m;
            }
        }

        return null;
    }

    // =================== RÉCUPÉRATION ADMIN ===================

    /**
     * Récupérer un administrateur par email
     */
    public Administrateur getAdministrateurParEmail(String email) throws SQLException {
        String query = """
            SELECT p.id, p.nom, p.prenom, p.email, p.telephone,
                   a.niveau_acces, a.date_nomination
            FROM personnes p
            INNER JOIN administrateurs a ON p.id = a.personne_id
            WHERE p.email = ? AND p.role = 'ADMIN'
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                NiveauAcces niveau = NiveauAcces.valueOf(rs.getString("niveau_acces"));

                Administrateur admin = new Administrateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        niveau
                );

                Date dateNomination = rs.getDate("date_nomination");
                if (dateNomination != null) {
                    admin.setDateNomination(dateNomination.toLocalDate());
                }

                return admin;
            }
        }

        return null;
    }

    // =================== INSCRIPTION ===================

    /**
     * Inscrire un nouveau membre
     */
    public Membre inscrireMembre(String nom, String prenom, String email,
                                 String telephone, String motDePasse) throws SQLException {
        // 1. Insérer dans personnes
        String queryPersonne = """
            INSERT INTO personnes (nom, prenom, email, telephone, mot_de_passe, role)
            VALUES (?, ?, ?, ?, ?, 'MEMBRE')
        """;

        try (PreparedStatement stmt = connection.prepareStatement(queryPersonne, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, telephone);
            stmt.setString(5, motDePasse);

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();

            if (keys.next()) {
                int id = keys.getInt(1);

                // 2. Créer un membre
                Membre membre = new Membre(id, nom, prenom, email, telephone);

                // 3. Insérer dans membres
                String queryMembre = """
                    INSERT INTO membres (personne_id, numero_membre, statut, cotisation_payee, montant_cotisation)
                    VALUES (?, ?, 'INACTIF', 0, 50.0)
                """;

                try (PreparedStatement stmtMembre = connection.prepareStatement(queryMembre)) {
                    stmtMembre.setInt(1, id);
                    stmtMembre.setString(2, membre.getNumeroMembre());
                    stmtMembre.executeUpdate();
                }

                return membre;
            }
        }

        throw new SQLException("Échec de l'inscription du membre");
    }

    /**
     * Inscrire un nouvel administrateur
     */
    public Administrateur inscrireAdministrateur(String nom, String prenom, String email,
                                                 String telephone, String motDePasse,
                                                 NiveauAcces niveau) throws SQLException {
        // 1. Insérer dans personnes
        String queryPersonne = """
            INSERT INTO personnes (nom, prenom, email, telephone, mot_de_passe, role)
            VALUES (?, ?, ?, ?, ?, 'ADMIN')
        """;

        try (PreparedStatement stmt = connection.prepareStatement(queryPersonne, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, telephone);
            stmt.setString(5, motDePasse);

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();

            if (keys.next()) {
                int id = keys.getInt(1);

                // 2. Créer un administrateur
                Administrateur admin = new Administrateur(id, nom, prenom, email, telephone, niveau);

                // 3. Insérer dans administrateurs
                String queryAdmin = """
                    INSERT INTO administrateurs (personne_id, niveau_acces, date_nomination)
                    VALUES (?, ?, ?)
                """;

                try (PreparedStatement stmtAdmin = connection.prepareStatement(queryAdmin)) {
                    stmtAdmin.setInt(1, id);
                    stmtAdmin.setString(2, niveau.name());
                    stmtAdmin.setDate(3, Date.valueOf(LocalDate.now()));
                    stmtAdmin.executeUpdate();
                }

                return admin;
            }
        }

        throw new SQLException("Échec de l'inscription de l'administrateur");
    }
}

