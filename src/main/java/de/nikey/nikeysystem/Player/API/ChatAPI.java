package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatAPI {

    private static final Component managementChannel = Component.text("[Management Channel] ").color(TextColor.color(50, 168, 98));

    public static void sendManagementMessage(Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                player.sendMessage(managementChannel.append(message.color(NamedTextColor.RED)));
            }
        }
    }
}
