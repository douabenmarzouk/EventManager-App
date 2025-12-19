package com.gestion.evenements.database.dao;

import com.gestion.evenements.database.DatabaseManager;
import com.gestion.evenements.model.membres.record.Feedback;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    private final Connection connection;

    public FeedbackDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    // === CREER UN FEEDBACK ===
    public Feedback creer(Feedback feedback, int associationId) throws SQLException {
        String query = """
            INSERT INTO feedbacks (membre_id, association_id, commentaire, note, date_feedback)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feedback.idMembre());
            stmt.setInt(2, associationId);
            stmt.setString(3, feedback.commentaire());
            stmt.setInt(4, feedback.note());
            stmt.setDate(5, Date.valueOf(feedback.date()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Feedback(

                            feedback.idMembre(),
                            feedback.note(),
                            feedback.commentaire(),
                            feedback.date()
                    );
                }
            }
        }
        return feedback;
    }

    public List<Feedback> getTousPourAssociation(int associationId) throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String query = """
            SELECT id, membre_id, note, commentaire, date_feedback
            FROM feedbacks
            WHERE association_id = ?
            ORDER BY date_feedback DESC
        """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, associationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbacks.add(new Feedback(

                            rs.getInt("membre_id"),
                            rs.getInt("note"),
                            rs.getString("commentaire"),
                            rs.getDate("date_feedback").toLocalDate()
                    ));
                }
            }
        }
        return feedbacks;
    }
}
