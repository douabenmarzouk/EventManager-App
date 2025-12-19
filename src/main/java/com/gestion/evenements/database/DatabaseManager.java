package com.gestion.evenements.database;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:gestion_evenements.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("✅ Connexion à la base de données établie");
            initialiserTables();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de récupérer la connexion à la base de données", e);
        }
        return connection;
    }


    private void initialiserTables() {
        try {
            Statement stmt = connection.createStatement();

            // Table Personnes (commune pour Membre et Administrateur)
            String createPersonnes = """
                CREATE TABLE IF NOT EXISTS personnes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    telephone TEXT,
                    mot_de_passe TEXT NOT NULL,
                    role TEXT NOT NULL CHECK(role IN ('MEMBRE', 'ADMIN')),
                    date_inscription DATE DEFAULT CURRENT_DATE
                )
            """;

            // Table Membres (spécifique)
            String createMembres = """
                CREATE TABLE IF NOT EXISTS membres (
                    personne_id INTEGER PRIMARY KEY,
                    numero_membre TEXT UNIQUE NOT NULL,
                    statut TEXT DEFAULT 'INACTIF' CHECK(statut IN ('ACTIF', 'INACTIF', 'SUSPENDU')),
                    cotisation_payee BOOLEAN DEFAULT 0,
                    montant_cotisation REAL DEFAULT 50.0,
                    date_paiement_cotisation DATE,
                    FOREIGN KEY (personne_id) REFERENCES personnes(id) ON DELETE CASCADE
                )
            """;

            // Table Administrateurs (spécifique)
            String createAdministrateurs = """
                CREATE TABLE IF NOT EXISTS administrateurs (
                    personne_id INTEGER PRIMARY KEY,
                    niveau_acces TEXT NOT NULL CHECK(niveau_acces IN ('MODERATEUR', 'ADMIN', 'SUPER_ADMIN')),
                    date_nomination DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY (personne_id) REFERENCES personnes(id) ON DELETE CASCADE
                )
            """;

            // Table Associations
            String createAssociations = """
                CREATE TABLE IF NOT EXISTS associations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    description TEXT,
                    localisation TEXT,
                    email TEXT UNIQUE NOT NULL,
                    mot_de_passe TEXT NOT NULL,
                    date_creation DATE DEFAULT CURRENT_DATE,
                    budget REAL DEFAULT 0.0,
                    solde REAL DEFAULT 0.0,
                    active BOOLEAN DEFAULT 1
                )
            """;

            // Table Feedbacks
            String createFeedbacks = """
                CREATE TABLE IF NOT EXISTS feedbacks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    membre_id INTEGER NOT NULL,
                    association_id INTEGER,
                    commentaire TEXT NOT NULL,
                    note INTEGER CHECK(note BETWEEN 1 AND 5),
                    date_feedback DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY (membre_id) REFERENCES membres(personne_id) ON DELETE CASCADE,
                    FOREIGN KEY (association_id) REFERENCES associations(id) ON DELETE SET NULL
                )
            """;

            // Table Historique Inscriptions
            String createHistorique = """
                CREATE TABLE IF NOT EXISTS historique_inscriptions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    membre_id INTEGER NOT NULL,
                    evenement TEXT NOT NULL,
                    date_inscription DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY (membre_id) REFERENCES membres(personne_id) ON DELETE CASCADE
                )
            """;

            // Table Association_Membres (relationMany-to-Many)
            String createAssociationMembres = """
                CREATE TABLE IF NOT EXISTS association_membres (
                    association_id INTEGER,
                    membre_id INTEGER,
                    date_ajout DATE DEFAULT CURRENT_DATE,
                    PRIMARY KEY (association_id, membre_id),
                    FOREIGN KEY (association_id) REFERENCES associations(id) ON DELETE CASCADE,
                    FOREIGN KEY (membre_id) REFERENCES membres(personne_id) ON DELETE CASCADE
                )
            """;

            stmt.execute(createPersonnes);
            stmt.execute(createMembres);
            stmt.execute(createAdministrateurs);
            stmt.execute(createAssociations);
            stmt.execute(createFeedbacks);
            stmt.execute(createHistorique);
            stmt.execute(createAssociationMembres);

            System.out.println("✅ Tables créées avec succès");

            // Créer un administrateur par défaut si nécessaire
            creerAdminParDefaut();

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la création des tables");
            e.printStackTrace();
        }
    }

    private void creerAdminParDefaut() {
        String checkAdmin = "SELECT COUNT(*) FROM personnes WHERE role = 'ADMIN'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkAdmin)) {

            if (rs.getInt(1) == 0) {
                String insertPersonne = """
                    INSERT INTO personnes (nom, prenom, email, telephone, mot_de_passe, role)
                    VALUES ('Admin', 'Super', 'admin@gestion.com', '0600000000', 'admin123', 'ADMIN')
                """;

                stmt.executeUpdate(insertPersonne, Statement.RETURN_GENERATED_KEYS);
                ResultSet keys = stmt.getGeneratedKeys();

                if (keys.next()) {
                    int adminId = keys.getInt(1);
                    String insertAdmin = """
                        INSERT INTO administrateurs (personne_id, niveau_acces)
                        VALUES (?, 'SUPER_ADMIN')
                    """;

                    PreparedStatement pstmt = connection.prepareStatement(insertAdmin);
                    pstmt.setInt(1, adminId);
                    pstmt.executeUpdate();

                    System.out.println("✅ Administrateur par défaut créé");
                    System.out.println("   Email: admin@gestion.com");
                    System.out.println("   Mot de passe: admin123");
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur création admin par défaut");
            e.printStackTrace();
        }
    }

    public void fermerConnexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connexion fermée");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}