package de.nikey.nikeysystem.DataBases;

import de.nikey.nikeysystem.Player.API.PermissionAPI;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class PermissionDatabase {
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NikeySystem/permissions.db");
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS player_roles (
                        uuid TEXT PRIMARY KEY,
                        role_name TEXT NOT NULL
                    );
                """);
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

    public static void setRole(UUID uuid, String roleName) {
        try (PreparedStatement ps = connection.prepareStatement("""
            INSERT OR REPLACE INTO player_roles (uuid, role_name) VALUES (?, ?);
        """)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, roleName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerRole(UUID uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM player_roles WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void loadAllPlayerRoles() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid, role_name FROM player_roles");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String role = rs.getString("role_name");
                PermissionAPI.playerRoles.put(uuid, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveAllPlayerRoles() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM player_roles;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection
                .prepareStatement("INSERT INTO player_roles (uuid, role_name) VALUES (?, ?)")) {

            for (Map.Entry<UUID, String> entry : PermissionAPI.playerRoles.entrySet()) {
                ps.setString(1, entry.getKey().toString());
                ps.setString(2, entry.getValue());
                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}