package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginModel {

    private static final String DB_URL = "jdbc:sqlite:vocabify.db";

    public LoginModel() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL" +
                    ")";
            stmt.execute(usersTable);

            String statsTable = "CREATE TABLE IF NOT EXISTS statistics (" +
                    "username TEXT PRIMARY KEY," +
                    "correct_answers INTEGER DEFAULT 0," +
                    "wrong_answers INTEGER DEFAULT 0," +
                    "FOREIGN KEY(username) REFERENCES users(username)" +
                    ")";
            stmt.execute(statsTable);

        } catch (SQLException e) {
            System.out.println("INIT DB FEHLER: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("AUTH FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // WICHTIG: Jetzt siehst du in der Konsole den echten Grund!
            System.out.println("REGISTER FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        if (username == null || newPassword == null) {
            return false;
        }

        username = username.trim();
        newPassword = newPassword.trim();

        if (username.isEmpty() || newPassword.isEmpty()) {
            return false;
        }

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("UPDATE PASSWORT FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String username) {
        if (username == null) {
            return false;
        }

        username = username.trim();

        if (username.isEmpty()) {
            return false;
        }

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
                System.out.println("DELETE USER FEHLER (Rollback): " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("DELETE USER FEHLER: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void updateStats(String username, boolean correct) {
        if (username == null) {
            return;
        }

        username = username.trim();
        if (username.isEmpty()) {
            return;
        }

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
            System.out.println("UPDATE STATS FEHLER: " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("GET STATS FEHLER: " + e.getMessage());
            e.printStackTrace();
        }

        return statsList;
    }

    public record UserStats(String username, int correct, int wrong) {}
}
