package de.nikey.nikeysystem.DataBases;

import de.nikey.nikeysystem.Player.API.Channel;
import de.nikey.nikeysystem.Player.Distributor.ChatDistributor;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ChannelDatabase {
    private static Connection connection;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/NikeySystem/channels.db");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS channels (" +
                        "id TEXT PRIMARY KEY," +
                        "name TEXT NOT NULL," +
                        "owner TEXT NOT NULL," +
                        "is_closed INTEGER NOT NULL," +
                        "messages TEXT," +
                        "members TEXT," +
                        "invited TEXT" +
                        ");");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveChannels() {
        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM channels;");
            }

            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO channels (id, name, owner, is_closed, messages, members, invited) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                for (Channel channel : ChatDistributor.channels.values()) {
                    ps.setString(1, channel.getId().toString());
                    ps.setString(2, channel.getName());
                    ps.setString(3, channel.getOwner().toString());
                    ps.setInt(4, channel.isClosed() ? 1 : 0);
                    ps.setString(5, String.join("§", channel.getMessages()));
                    ps.setString(6, channel.getMembers().stream().map(UUID::toString).collect(Collectors.joining(",")));
                    ps.setString(7, channel.getInvitedPlayers().stream().map(UUID::toString).collect(Collectors.joining(",")));
                    ps.addBatch();
                }

                ps.executeBatch();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadChannels() {
        ChatDistributor.channels.clear();
        ChatDistributor.playerChannels.clear();

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM channels");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String name = rs.getString("name");
                UUID owner = UUID.fromString(rs.getString("owner"));
                boolean isClosed = rs.getInt("is_closed") == 1;

                List<String> messages = new ArrayList<>();
                String rawMessages = rs.getString("messages");
                if (rawMessages != null && !rawMessages.isEmpty())
                    messages = new ArrayList<>(Arrays.asList(rawMessages.split("§")));

                Set<UUID> members = new HashSet<>();
                String rawMembers = rs.getString("members");
                if (rawMembers != null && !rawMembers.isEmpty())
                    members = Arrays.stream(rawMembers.split(",")).map(UUID::fromString).collect(Collectors.toSet());

                Set<UUID> invited = new HashSet<>();
                String rawInvited = rs.getString("invited");
                if (rawInvited != null && !rawInvited.isEmpty())
                    invited = Arrays.stream(rawInvited.split(",")).map(UUID::fromString).collect(Collectors.toSet());

                Channel channel = new Channel(id, name, owner, isClosed, messages, members);
                channel.getInvitedPlayers().addAll(invited);

                ChatDistributor.channels.put(id, channel);
                for (UUID member : members) {
                    ChatDistributor.playerChannels.put(member, id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}