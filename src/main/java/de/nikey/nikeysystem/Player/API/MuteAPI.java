package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MuteAPI {
    private static HashMap<String, Long> mutedPlayers = new HashMap<>();

    public static void add(String player, long endTime) {
        mutedPlayers.put(player, endTime);
    }

    public static void remove(String player) {
        mutedPlayers.remove(player);
    }

    public static Set<String> getMutedPlayers() {
        return mutedPlayers.keySet();
    }

    public static long getMutedDuration(String player) {
        Long l = mutedPlayers.get(player);
        if (l == 0) {
            return l;
        }
        l = l-System.currentTimeMillis();
        l = l/1000;
        return l;
    }

    public static boolean isMuted(String player) {
        if (mutedPlayers.containsKey(player)) {
            long endTime = mutedPlayers.get(player);
            if (endTime == 0 || endTime > System.currentTimeMillis()) {
                return true;
            } else {
                mutedPlayers.remove(player);
                return false;
            }
        }
        return false;
    }

    public static void saveMutedPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        // Entferne alte Mute-Daten
        config.set("mutedPlayers", null);

        // Speichere alle aktuellen Mute-Daten
        for (Map.Entry<String, Long> entry : mutedPlayers.entrySet()) {
            config.set("mutedPlayers." + entry.getKey(), entry.getValue());
        }

        // Speichere die config.yml
        NikeySystem.getPlugin().saveConfig();
    }

    // Lade die gemuteten Spieler aus der config.yml
    public static void loadMutedPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        if (config.contains("mutedPlayers")) {
            for (String playerName : config.getConfigurationSection("mutedPlayers").getKeys(false)) {
                long muteEndTime = config.getLong("mutedPlayers." + playerName);
                mutedPlayers.put(playerName, muteEndTime);
            }
        }
    }
}
