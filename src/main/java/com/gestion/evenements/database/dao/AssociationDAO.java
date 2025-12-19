package com.gestion.evenements.database.dao;

import com.gestion.evenements.database.DatabaseManager;
import com.gestion.evenements.model.membres.entities.Association;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.exceptions.MembreException;

import java.sql.*;
import java.time.LocalDate;

public class AssociationDAO {

    private final Connection connection;
    private final MembreDAO membreDAO; // Pour charger les membres

    public AssociationDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
        this.membreDAO = new MembreDAO(); // ou injection si tu utilises DI
    }

    // ======================= CRÉATION =======================
    public Association creerAssociation(String nom, String description, String localisation,
                                        String email, String motDePasse, double budget) throws SQLException {

        String query = """
            INSERT INTO associations (nom, description, localisation, email, mot_de_passe, budget, solde, active, date_creation)
            VALUES (?, ?, ?, ?, ?, ?, 0.0, true, CURRENT_DATE)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nom);
            stmt.setString(2, description);
            stmt.setString(3, localisation);
            stmt.setString(4, email);
            // À l'avenir : remplace par BCrypt !
            stmt.setString(5, motDePasse);
            stmt.setDouble(6, budget);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création de l'association");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    return findByIdAvecMembres(id); // Charge tout
                } else {
                    throw new SQLException("Création échouée, aucun ID généré");
                }
            }
        }
    }

    // ======================= RECHERCHE =======================
    public Association findById(int id) throws SQLException {
        String query = "SELECT * FROM associations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapToAssociation(rs, false) : null;
            }
        }
    }

    public Association findByIdAvecMembres(int id) throws SQLException {
        Association asso = findById(id);
        if (asso != null) {
            chargerMembres(asso);
        }
        return asso;
    }

    public Association finddByEmail(String email) throws SQLException {
        String query = "SELECT * FROM associations WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapToAssociation(rs, false) : null;
            }
        }
    }
    // Ajoute cette méthode dans AssociationDAO (en plus de findById)
    public Association getParId(int id) throws SQLException {
        return findById(id);
    }

    public Association getParIdAvecMembres(int id) throws SQLException {
        return findByIdAvecMembres(id);
    }
    // Méthode de paiement sécurisée (remplace mettreAJourSolde)

    public Association findByEmailAvecMembres(String email) throws SQLException {
        Association asso = finddByEmail(email);
        if (asso != null) {
            chargerMembres(asso);
        }
        return asso;
    }
    public void mettreAJourSolde(int associationId, double nouveauSolde) throws SQLException {
        String query = "UPDATE associations SET solde = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, nouveauSolde);
            stmt.setInt(2, associationId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Association non trouvée (id=" + associationId + ")");
            }
        }
    }
    // Méthode conservée pour compatibilité avec ton ancien code de connexion
    public boolean verifierMotDePasse(String email, String motDePasseEntre) throws SQLException {
        String sql = "SELECT mot_de_passe FROM associations WHERE LOWER(email) = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String mdpStocke = rs.getString("mot_de_passe").trim();
                    System.out.println("DEBUG -> mot de passe stocké : '" + mdpStocke + "'");
                    System.out.println("DEBUG -> mot de passe entré   : '" + motDePasseEntre + "'");
                    return motDePasseEntre.equals(mdpStocke);
                } else {
                    System.out.println("DEBUG -> Email non trouvé : " + email);
                }
            }
        }
        return false;
    }



    // Méthode conservée pour compatibilité
    public Association getParEmail(String email) throws SQLException {
        return finddByEmail(email);
    }

    // Méthode conservée pour compatibilité (avec membres chargés)
    public Association getParEmailAvecMembres(String email) throws SQLException {
        return findByEmailAvecMembres(email);
    }

    // ======================= AUTHENTIFICATION =======================
    public Association authentifier(String email, String motDePasse) throws SQLException {
        String query = "SELECT * FROM associations WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse); // TODO : utiliser BCrypt à l'avenir
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Association asso = mapToAssociation(rs, true);
                    chargerMembres(asso);
                    return asso;
                }
            }
        }
        return null;
    }

    // ======================= MISES À JOUR =======================
    public void mettreAJour(Association association) throws SQLException {
        String query = """
            UPDATE associations 
            SET nom = ?, description = ?, localisation = ?, email = ?, 
                budget = ?, solde = ?, active = ?
            WHERE id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, association.getNom());
            stmt.setString(2, association.getDescription());
            stmt.setString(3, association.getLocalisation());
            stmt.setString(4, association.getEmail());
            stmt.setDouble(5, association.getBudget());
            stmt.setDouble(6, association.getSolde());
            stmt.setBoolean(7, association.isActive());
            stmt.setInt(8, association.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Mise à jour du solde avec transaction sécurisée
     */
    public void enregistrerPaiementCotisation(int associationId, int membreId, double montant) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Mettre à jour le solde de l'association
            String updateSolde = "UPDATE associations SET solde = solde + ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateSolde)) {
                stmt.setDouble(1, montant);
                stmt.setInt(2, associationId);
                stmt.executeUpdate();
            }

            // Mettre à jour le statut de cotisation du membre
            membreDAO.marquerCotisationPayee(membreId, montant);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // ======================= SUPPRESSION =======================
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM associations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ======================= UTILITAIRES =======================
    private Association mapToAssociation(ResultSet rs, boolean chargerMembres) throws SQLException {
        String dateStr = rs.getString("date_creation"); // Récupère la date comme texte
        LocalDate dateCreation = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            dateCreation = LocalDate.parse(dateStr); // Parse YYYY-MM-DD en LocalDate
        }


        Association asso = new Association(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"),
                rs.getString("localisation"),
                rs.getString("email"),
                dateCreation,
                rs.getDouble("budget"),
                rs.getBoolean("active")
        );

        asso.setSolde(rs.getDouble("solde")); // Maintenant possible grâce au setter

        if (chargerMembres) {
            chargerMembres(asso);
        }

        return asso;
    }

    private void chargerMembres(Association association) {
        try {
            var membres = membreDAO.findByAssociationId(association.getId());
            association.getMembres().setAll(membres); // ← Plus d'ambiguïté + plus propre
        } catch (SQLException e) {
            System.err.println("Erreur chargement membres : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
