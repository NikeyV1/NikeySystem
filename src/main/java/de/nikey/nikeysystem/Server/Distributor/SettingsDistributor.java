package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.Settings.ServerSettings;
import org.bukkit.entity.Player;

public class SettingsDistributor {

    public static void settingsDistributor(Player player, String[] args) {
        String basePerm = "system.server.settings.";
        if (args[3].equalsIgnoreCase("open")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "open") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            ServerSettings.openSettingsInventory(player);
        }
    }
}
