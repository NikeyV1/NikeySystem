package de.nikey.nikeysystem.DataBases;

import de.nikey.nikeysystem.Player.API.HideAPI;

import java.sql.*;
import java.util.UUID;

public class HideDatabase {
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NikeySystem/hide_data.db");
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS hide_status (" +
                        "uuid TEXT NOT NULL," +
                        "type TEXT NOT NULL," +
                        "PRIMARY KEY (uuid, type));");
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

    public static void saveAll() {
        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM hide_status;");
            }

            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO hide_status (uuid, type) VALUES (?, ?)")) {
                for (UUID uuid : HideAPI.getHiddenPlayers()) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, "HIDDEN");
                    ps.addBatch();
                }
                for (UUID uuid : HideAPI.getTrueHidePlayers()) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, "TRUEHIDE");
                    ps.addBatch();
                }
                for (UUID uuid : HideAPI.getImmunityPlayers()) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, "IMMUNE");
                    ps.addBatch();
                }

                ps.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveChanges() {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT OR IGNORE INTO hide_status (uuid, type) VALUES (?, ?)")) {

                for (UUID uuid : HideAPI.getChangedPlayers()) {
                    String type = HideAPI.getTypeOf(uuid);
                    if (type == null) continue;
                    insert.setString(1, uuid.toString());
                    insert.setString(2, type);
                    insert.addBatch();
                }

                insert.executeBatch();
            }

            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM hide_status WHERE uuid = ?")) {
                for (UUID uuid : HideAPI.getRemovedPlayers()) {
                    delete.setString(1, uuid.toString());
                    delete.addBatch();
                }

                delete.executeBatch();
            }

            connection.commit();

            // Wichtig: Nach dem Commit aufräumen
            HideAPI.clearChangedAndRemoved();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void loadAll() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid, type FROM hide_status");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String type = rs.getString("type");

                switch (type) {
                    case "HIDDEN" -> HideAPI.getHiddenPlayers().add(uuid);
                    case "TRUEHIDE" -> HideAPI.getTrueHidePlayers().add(uuid);
                    case "IMMUNE" -> HideAPI.getImmunityPlayers().add(uuid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
