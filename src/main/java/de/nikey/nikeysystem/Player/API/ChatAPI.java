package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Map;
import java.util.UUID;

import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.channels;
import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.dataFile;

public class ChatAPI {

    private static final Component managementChannel = Component.text("[Management Channel] ").color(TextColor.color(50, 168, 98));

    public static final TextColor infoColor = TextColor.color(30, 144, 255);
    public static final TextColor errorColor = NamedTextColor.RED;
    public static final TextColor criticalErrorColor = TextColor.color(178, 34, 34);

    public static void sendManagementMessage(Component message, ManagementType type) {

        TextColor color;

        switch (type) {
            case INFO -> color = TextColor.color(30, 144, 255); // Dodger Blue
            case ERROR -> color = NamedTextColor.RED; // Bright Red
            case CRITICAL_ERROR -> color = TextColor.color(178, 34, 34);
            case MINOR_ERROR -> color = TextColor.color(237, 121, 33);
            default -> color = NamedTextColor.WHITE; // Fallback color
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                player.sendMessage(managementChannel.append(message.color(color)));
            }
        }
    }

    public static void sendManagementMessage(Component message, ManagementType type, boolean useCustomColor) {

        if (useCustomColor) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                    player.sendMessage(managementChannel.append(message));
                }
            }
            return;
        }
        TextColor color;

        switch (type) {
            case INFO -> color = TextColor.color(30, 144, 255); // Dodger Blue
            case ERROR -> color = NamedTextColor.RED; // Bright Red
            case CRITICAL_ERROR -> color = TextColor.color(178, 34, 34);
            case MINOR_ERROR -> color = TextColor.color(237, 121, 33);
            default -> color = NamedTextColor.WHITE; // Fallback color
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                player.sendMessage(managementChannel.append(message.color(color)));
            }
        }
    }

    public static void sendManagementActionBar(Component message, ManagementType type) {
        TextColor color;

        if (type != ManagementType.CUSTOM) {
            switch (type) {
                case INFO -> color = TextColor.color(30, 144, 255); // Dodger Blue
                case ERROR -> color = NamedTextColor.RED; // Bright Red
                case CRITICAL_ERROR -> color = TextColor.color(178, 34, 34);
                case MINOR_ERROR -> color = TextColor.color(237, 121, 33);
                default -> color = NamedTextColor.WHITE; // Fallback color
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                    player.sendActionBar(managementChannel.append(message.color(color)));
                }
            }
        }else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                    player.sendActionBar(managementChannel.append(message));
                }
            }
        }
    }

    public static void saveChannels() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            out.writeObject(channels);
        } catch (IOException e) {
            sendManagementMessage(Component.text("Failed to save channels: " + e.getMessage()), ManagementType.ERROR,true);
        }
    }

    public static void loadChannels() {
        if (!dataFile.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile))) {
            Object readObject = in.readObject();

            if (readObject instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<UUID, Channel> loadedChannels = (Map<UUID, Channel>) readObject;
                channels.putAll(loadedChannels);
            } else {
                sendManagementMessage(Component.text("Invalid data format in file load channels"), ManagementType.ERROR);
            }
        } catch (IOException | ClassNotFoundException e) {
            sendManagementMessage(Component.text("Failed to load channels: " + e.getMessage()), ManagementType.ERROR,true);
        }
    }

    public enum ManagementType {
        INFO,
        MINOR_ERROR,
        ERROR,
        CRITICAL_ERROR,
        CUSTOM
    }
}
