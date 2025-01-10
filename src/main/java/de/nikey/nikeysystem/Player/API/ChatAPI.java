package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Map;
import java.util.UUID;

import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.*;

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
                if (PermissionAPI.isManagement(player.getName())) {
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

    public static void loadChannels() {
        File channelsFile = new File(NikeySystem.getPlugin().getDataFolder(), "channels.yml");

        if (!channelsFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(channelsFile);
        channels.clear();

        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                Map<String, Object> serializedData = config.getConfigurationSection(key).getValues(false);
                Channel channel = Channel.deserialize(serializedData);
                channels.put(uuid, channel);
                for (UUID id : channel.getMembers()) {
                    playerChannels.put(id,channel.getId());
                }
            } catch (IllegalArgumentException e) {
                sendManagementMessage(Component.text("Failed to save channels: " + e.getMessage()), ManagementType.ERROR, true);
            }
        }
    }




    public static void saveChannels() {

        File channelsFile = new File(NikeySystem.getPlugin().getDataFolder(), "channels.yml");

        if (!channelsFile.exists()) {
            try {
                channelsFile.createNewFile();
            } catch (IOException ignored) {}
        }

        YamlConfiguration config = new YamlConfiguration();

        // Map serialisieren
        for (Map.Entry<UUID, Channel> entry : channels.entrySet()) {
            String key = entry.getKey().toString();
            Channel channel = entry.getValue();
            config.set(key, channel.serialize());
        }

        try {
            config.save(channelsFile);
        } catch (IOException e) {
            sendManagementMessage(Component.text("Failed to load channels: " + e.getMessage()), ManagementType.ERROR, true);
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
