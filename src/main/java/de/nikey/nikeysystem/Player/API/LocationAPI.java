package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationAPI {
    public static Map<String, Location> guardLocations = new HashMap<>();
    public static Map<String, Double> guardRanges = new HashMap<>();
    public static Map<Player, String> playerInGuardArea = new HashMap<>();
    public static Map<String, String > guardCreators = new HashMap<>();

    public static void setGuardCreator(String guardName, Player player) {
        guardCreators.put(guardName, player.getName());
    }

    public static boolean isPlayerExcluded(Player player, String guardName) {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        List<String> includeList = config.getStringList("location.settings." + guardName + ".include");
        List<String> excludeList = config.getStringList("location.settings." + guardName + ".exclude");

        if (guardCreators.get(guardName).equalsIgnoreCase(player.getName()))return false;

        // Spieler in der Exclude-Liste dürfen nichts tun
        if (excludeList.contains(player.getName())) {
            return true;
        }

        // Spieler in der Include-Liste dürfen alles
        return !includeList.contains(player.getName());
    }
}
