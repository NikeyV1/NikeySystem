package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

    public enum ManagementType {
        INFO,
        MINOR_ERROR,
        ERROR,
        CRITICAL_ERROR,
        CUSTOM
    }
}
