package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.DataBases.ChannelDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ChatAPI {

    private static final Component managementChannel = Component.text("[Management]").color(TextColor.color(50, 168, 98));

    public static void sendManagementMessage(Component message, ManagementType type) {
        Component subComponent = Component.text(" [Info] ").color(TextColor.color(30, 144, 255));

        if (type == ManagementType.ERROR) {
            subComponent = Component.text(" [Error] ").color(TextColor.color(255, 85, 85));
        }else if (type == ManagementType.CRITICAL_ERROR) {
            subComponent = Component.text(" [Critical Error] ").color(TextColor.color(178, 34, 34));
        }else if (type == ManagementType.MINOR_ERROR) {
            subComponent = Component.text(" [Minor Error] ").color(TextColor.color(237, 121, 33));
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PermissionAPI.isManagement(player.getName())) {
                player.sendMessage(managementChannel.append(subComponent).append(message));
            }
        }
    }

    public static void chatStartup() {
        ChannelDatabase.connect();
        ChannelDatabase.loadChannels();
    }

    public static void chatShutdown() {
        ChannelDatabase.saveChannels();
        ChannelDatabase.disconnect();
    }

    public enum ManagementType {
        INFO,
        MINOR_ERROR,
        ERROR,
        CRITICAL_ERROR,
    }
}