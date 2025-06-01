package de.nikey.nikeysystem.DataBases;

import java.sql.*;

public class BackupDatabase {
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NikeySystem/backup_data.db");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS backup_settings (" +
                        "key TEXT PRIMARY KEY," +
                        "value TEXT NOT NULL);");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveSetting(String key, String value) {
        try (PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO backup_settings (key, value) VALUES (?, ?)")) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String loadSetting(String key) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT value FROM backup_settings WHERE key = ?")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeSetting(String key) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM backup_settings WHERE key = ?")) {
            ps.setString(1, key);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
