package de.nikey.nikeysystem.DataBases;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ModerationAPI;
import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.API.Punishment;

import java.sql.*;
import java.util.*;

public class PunishmentDatabase {
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NikeySystem/punishments.db");

            try (Statement stmt = connection.createStatement()) {
                // Tabelle für Frozen-Spieler
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS frozen_players (" +
                        "uuid TEXT PRIMARY KEY);");

                // Tabelle für Mute-Spieler
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS muted_players (" +
                        "uuid TEXT PRIMARY KEY," +
                        "end_time INTEGER NOT NULL);");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS punishment_history (" +
                        "uuid TEXT NOT NULL," +
                        "type TEXT NOT NULL," +
                        "reason TEXT NOT NULL," +
                        "start_time INTEGER NOT NULL," +
                        "duration INTEGER NOT NULL," +
                        "is_permanent INTEGER NOT NULL);");
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

    // ---------------- FREEZE ----------------

    public static void saveFrozenPlayer(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO frozen_players (uuid) VALUES (?);")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeFrozenPlayer(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM frozen_players WHERE uuid = ?;")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadAllFrozenPlayers() {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT uuid FROM frozen_players");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    ModerationAPI.freezePlayer(uuid);
                } catch (IllegalArgumentException e) {
                    NikeySystem.getPlugin().getLogger().info("Ungültige UUID in DB: " + rs.getString("uuid"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------- MUTE ----------------

    public static void saveMutedPlayer(UUID uuid, long endTime) {
        try (PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO muted_players (uuid, end_time) VALUES (?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, endTime);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMutedPlayer(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM muted_players WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadAllMutedPlayers() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid, end_time FROM muted_players");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String raw = rs.getString("uuid");
                UUID uuid;
                try {
                    uuid = UUID.fromString(raw);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                long endTime = rs.getLong("end_time");
                MuteAPI.add(uuid, endTime);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ---------------- PUNISHMENT HISTORY ----------------

    public static void savePunishmentHistory(Punishment punishment) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO punishment_history (uuid, type, reason, start_time, duration, is_permanent) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, punishment.getPlayerUUID().toString());
            ps.setString(2, punishment.getType().name());
            ps.setString(3, punishment.getReason());
            ps.setLong(4, punishment.getStartTime());
            ps.setLong(5, punishment.getDuration());
            ps.setInt(6, punishment.isPermanent() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Punishment> loadPunishmentHistory(UUID uuid) {
        List<Punishment> history = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT type, reason, start_time, duration, is_permanent FROM punishment_history WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Punishment.PunishmentType type = Punishment.PunishmentType.fromString(rs.getString("type"));
                String reason = rs.getString("reason");
                long startTime = rs.getLong("start_time");
                long duration = rs.getLong("duration");
                boolean isPermanent = rs.getInt("is_permanent") == 1;

                history.add(new Punishment(uuid, type, reason, startTime, duration, isPermanent));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static Map<UUID, List<Punishment>> loadAllPunishmentHistories() {
        Map<UUID, List<Punishment>> allHistories = new HashMap<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT uuid, type, reason, start_time, duration, is_permanent FROM punishment_history")) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                Punishment.PunishmentType type = Punishment.PunishmentType.fromString(rs.getString("type"));
                String reason = rs.getString("reason");
                long startTime = rs.getLong("start_time");
                long duration = rs.getLong("duration");
                boolean isPermanent = rs.getInt("is_permanent") == 1;

                Punishment punishment = new Punishment(uuid, type, reason, startTime, duration, isPermanent);
                allHistories.computeIfAbsent(uuid, k -> new ArrayList<>()).add(punishment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allHistories;
    }

    public static void loadAllData() {
        loadAllFrozenPlayers();
        loadAllMutedPlayers();
        //NikeySystem.getManager().loadAllHistories();
    }
}