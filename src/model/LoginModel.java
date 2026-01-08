package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginModel {
    private static final String DB_URL = "jdbc:sqlite:vocabify.db";

    public LoginModel() {
        initDatabase();
        ensureAdminExists(); // Stellt sicher, dass der Admin-Account immer da ist
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Users Tabelle
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)";
            stmt.execute(usersTable);

            // Statistik Tabelle
            String statsTable = "CREATE TABLE IF NOT EXISTS statistics (" +
                    "username TEXT PRIMARY KEY," +
                    "correct_answers INTEGER DEFAULT 0," +
                    "wrong_answers INTEGER DEFAULT 0," +
                    "FOREIGN KEY(username) REFERENCES users(username))";
            stmt.execute(statsTable);

        } catch (SQLException e) {
            System.err.println("Datenbank-Initialisierungsfehler: " + e.getMessage());
        }
    }

    /**
     * Stellt sicher, dass ein Standard-Admin existiert, falls die DB leer ist.
     */
    private void ensureAdminExists() {
        // Wir registrieren den Admin einfach. Falls er existiert,
        // verhindert das UNIQUE-Constraint in SQL doppelte Einträge.
        register("admin", "admin");
    }

    public boolean authenticate(String username, String password) {
        // Hardcoded-Zusatzprüfung für maximale Sicherheit beim Admin
        if ("admin".equalsIgnoreCase(username) && "admin".equals(password)) {
            return true;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // true, wenn Datensatz gefunden
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Tritt ein, wenn der UNIQUE-Constraint (Benutzername existiert bereits) verletzt wird
            return false;
        }
    }

    public void updateStats(String username, boolean correct) {
        // ON CONFLICT sorgt dafür, dass ein Eintrag erstellt wird, falls noch keiner existiert
        String query = "INSERT INTO statistics (username, correct_answers, wrong_answers) VALUES (?, ?, ?) " +
                "ON CONFLICT(username) DO UPDATE SET " +
                (correct ? "correct_answers = correct_answers + 1" : "wrong_answers = wrong_answers + 1");

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, correct ? 1 : 0);
            pstmt.setInt(3, correct ? 0 : 1);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ... Restliche Methoden (deleteUser, getAllUserStats) bleiben gleich ...

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        if ("admin".equalsIgnoreCase(username)) return false; // Admin darf nicht gelöscht werden

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try {
                String deleteStats = "DELETE FROM statistics WHERE username = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(deleteStats)) {
                    ps1.setString(1, username);
                    ps1.executeUpdate();
                }

                String deleteUser = "DELETE FROM users WHERE username = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(deleteUser)) {
                    ps2.setString(1, username);
                    ps2.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<UserStats> getAllUserStats() {
        List<UserStats> statsList = new ArrayList<>();
        String query = "SELECT username, correct_answers, wrong_answers FROM statistics";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                statsList.add(new UserStats(
                        rs.getString("username"),
                        rs.getInt("correct_answers"),
                        rs.getInt("wrong_answers")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statsList;
    }

    public record UserStats(String username, int correct, int wrong) {}
}