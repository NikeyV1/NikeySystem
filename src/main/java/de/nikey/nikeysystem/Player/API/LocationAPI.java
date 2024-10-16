package de.nikey.nikeysystem.Player.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LocationAPI {
    public static Map<String, Location> guardLocations = new HashMap<>();
    public static Map<String, Double> guardRanges = new HashMap<>();
    public static Map<Player, String> playerInGuardArea = new HashMap<>();
    public static Map<String, Player> guardCreators = new HashMap<>();

    public static void setGuardCreator(String guardName, Player player) {
        guardCreators.put(guardName, player);
    }
}
