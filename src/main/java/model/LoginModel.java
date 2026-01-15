package model;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginModel {
    // Die Datenbank wird im Home-Verzeichnis gespeichert, damit sie beschreibbar ist
    private static final String DB_NAME = "vocabify.db";
    private static final String DB_PATH = System.getProperty("user.home") + File.separator + DB_NAME;
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    // Statischer Block: Lädt den Treiber und verhindert den SLF4J-Absturz
    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite-Treiber erfolgreich geladen.");
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler: SQLite-Treiber oder SLF4J-Bibliothek nicht gefunden!");
            e.printStackTrace();
        }
    }

    public LoginModel() {
        prepareDatabaseFile();
        initDatabase();
        ensureAdminExists();
    }

    /**
     * Kopiert die Datenbank-Vorlage aus den Resources (Read-Only)
     * in das Benutzerverzeichnis (Beschreibbar).
     */
    private void prepareDatabaseFile() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            // Sucht die Vorlage im src/main/resources Ordner
            try (InputStream in = getClass().getResourceAsStream("/" + DB_NAME)) {
                if (in != null) {
                    Files.copy(in, dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Datenbank-Vorlage nach " + DB_PATH + " kopiert.");
                } else {
                    // Falls keine Vorlage existiert, erstelle eine neue Datei
                    dbFile.createNewFile();
                    System.out.println("Leere Datenbank-Datei erstellt unter: " + DB_PATH);
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Vorbereiten der DB-Datei: " + e.getMessage());
            }
        }
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Tabellen erstellen, falls sie noch nicht existieren
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS statistics (" +
                    "username TEXT PRIMARY KEY," +
                    "correct_answers INTEGER DEFAULT 0," +
                    "wrong_answers INTEGER DEFAULT 0," +
                    "FOREIGN KEY(username) REFERENCES users(username))");

        } catch (SQLException e) {
            System.err.println("Datenbank-Initialisierungsfehler: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        // Einfacher Admin-Check
        if ("admin".equalsIgnoreCase(username) && "admin".equals(password)) {
            return true;
        }
        if ("Tester".equalsIgnoreCase(username) && "test".equals(password)) {
            return true;
        }

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Gibt true zurück, wenn User/Passwort-Kombi existiert
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
            return false; // User existiert wahrscheinlich schon
        }
    }

    public void updateStats(String username, boolean correct) {
        // SQLite "Upsert": Einfügen oder bei Konflikt Updaten
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

    private void ensureAdminExists() {
        register("admin", "admin");
    }

    // --- Hilfsmethoden für Passwort-Änderung und User-Statistiken ---

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