package com.gestion.evenements.database.dao;

import com.gestion.evenements.database.DatabaseManager;
import com.gestion.evenements.model.membres.entities.Membre;
import com.gestion.evenements.model.membres.enums.StatutMembre;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembreDAO {

    private final Connection connection;

    public MembreDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    // ===================================================================
    // MÉTHODES EXISTANTES (tu les gardes telles quelles – elles sont parfaites)
    // ===================================================================

    public Membre creer(Membre membre, String motDePasse) throws SQLException {
        // ... ton code existant (inchangé) ...
        String queryPersonne = """
            INSERT INTO personnes (nom, prenom, email, telephone, mot_de_passe, role)
            VALUES (?, ?, ?, ?, ?, 'MEMBRE')
        """;

        try (PreparedStatement stmt = connection.prepareStatement(queryPersonne, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getEmail());
            stmt.setString(4, membre.getTelephone());
            stmt.setString(5, motDePasse);

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();

            if (keys.next()) {
                int id = keys.getInt(1);
                membre.setId(id);

                String queryMembre = """
                    INSERT INTO membres (personne_id, numero_membre, statut, cotisation_payee, montant_cotisation)
                    VALUES (?, ?, ?, ?, ?)
                """;

                try (PreparedStatement stmtMembre = connection.prepareStatement(queryMembre)) {
                    stmtMembre.setInt(1, id);
                    stmtMembre.setString(2, membre.getNumeroMembre());
                    stmtMembre.setString(3, membre.getStatut().name());
                    stmtMembre.setBoolean(4, membre.isCotisationPayee());
                    stmtMembre.setDouble(5, membre.getMontantCotisation());
                    stmtMembre.executeUpdate();
                }
            }
            return membre;
        }
    }

    public void mettreAJour(Membre membre) throws SQLException {
        // ... ton code existant (inchangé) ...
        String queryPersonne = """
            UPDATE personnes 
            SET nom = ?, prenom = ?, email = ?, telephone = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(queryPersonne)) {
            stmt.setString(1, membre.getNom());
            stmt.setString(2, membre.getPrenom());
            stmt.setString(3, membre.getEmail());
            stmt.setString(4, membre.getTelephone());
            stmt.setInt(5, membre.getId());
            stmt.executeUpdate();
        }

        String queryMembre = """
            UPDATE membres 
            SET statut = ?, cotisation_payee = ?, montant_cotisation = ?, date_paiement_cotisation = ?
            WHERE personne_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(queryMembre)) {
            stmt.setString(1, membre.getStatut().name());
            stmt.setBoolean(2, membre.isCotisationPayee());
            stmt.setDouble(3, membre.getMontantCotisation());

            if (membre.getDatePaiementCotisation() != null) {
                stmt.setDate(4, Date.valueOf(membre.getDatePaiementCotisation()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            stmt.setInt(5, membre.getId());
            stmt.executeUpdate();
        }
    }

    public void supprimer(int membreId) throws SQLException {
        // Tu devrais aussi supprimer de association_membres et membres avant personnes
        // Mais on garde simple pour l'instant
        String query = "DELETE FROM personnes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, membreId);
            stmt.executeUpdate();
        }
    }

    public void ajouterAAssociation(int membreId, int associationId) throws SQLException {
        String query = """
            INSERT INTO association_membres (association_id, membre_id)
            VALUES (?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, associationId);
            stmt.setInt(2, membreId);
            stmt.executeUpdate();
        }
    }

    // ===================================================================
    // NOUVELLES MÉTHODES REQUISES PAR AssociationDAO (AJOUTÉES ICI)
    // ===================================================================

    /**
     * Récupère tous les membres d'une association (utilisée pour charger l'ObservableList)
     */
    public List<Membre> findByAssociationId(int associationId) throws SQLException {
        // Cette méthode remplace simplement getTousPourAssociation avec un nom plus standard
        return getTousPourAssociation(associationId);
    }

    /**
     * Méthode déjà excellente – on la garde et on la rend publique avec le bon nom
     */
    public List<Membre> getTousPourAssociation(int associationId) throws SQLException {
        List<Membre> membres = new ArrayList<>();

        String query = """
            SELECT p.id, p.nom, p.prenom, p.email, p.telephone,
                   m.numero_membre, m.statut, m.cotisation_payee, 
                   m.montant_cotisation, m.date_paiement_cotisation
            FROM personnes p
            INNER JOIN membres m ON p.id = m.personne_id
            INNER JOIN association_membres am ON m.personne_id = am.membre_id
            WHERE am.association_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, associationId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
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

                membres.add(m);
            }
        }
        return membres;
    }

    /**
     * Marque la cotisation comme payée + met à jour montant et date
     * Appelée dans la transaction de AssociationDAO
     */
    public void marquerCotisationPayee(int membreId, double montant) throws SQLException {
        String query = """
            UPDATE membres 
            SET cotisation_payee = true, 
                montant_cotisation = ?, 
                date_paiement_cotisation = CURRENT_DATE
            WHERE personne_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, montant);
            stmt.setInt(2, membreId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Membre non trouvé lors du paiement cotisation (id=" + membreId + ")");
            }
        }
    }
}