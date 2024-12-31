package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.Server.Settings.ServerSettings;
import org.bukkit.entity.Player;

public class SettingsDistributor {

    public static void settingsDistributor(Player player, String[] args) {
        if (args[3].equalsIgnoreCase("open")) {
            ServerSettings.openSettingsInventory(player);
        }
    }
}
