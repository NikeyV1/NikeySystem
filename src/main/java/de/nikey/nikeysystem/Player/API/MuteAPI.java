package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        config.set("mutedPlayers", null);

        for (Map.Entry<String, Long> entry : mutedPlayers.entrySet()) {
            config.set("mutedPlayers." + entry.getKey(), entry.getValue());
        }

        NikeySystem.getPlugin().saveConfig();
    }

    public static void loadMutedPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        if (config.contains("mutedPlayers")) {
            for (String playerName : config.getConfigurationSection("mutedPlayers").getKeys(false)) {
                long muteEndTime = config.getLong("mutedPlayers." + playerName);
                mutedPlayers.put(playerName, muteEndTime);
            }
        }
    }

    public static int parseTime(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Time input cannot be null or empty");
        }

        // RegEx: Zahlen gefolgt von einem optionalen Suffix (s, m, h, d, w)
        Pattern pattern = Pattern.compile("^(\\d+)([smhdw]?)$");
        Matcher matcher = pattern.matcher(input.toLowerCase());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time format. Use a number followed by s, m, h, d, or w");
        }

        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" -> value; // Sekunden
            case "m" -> value * 60; // Minuten
            case "h" -> value * 60 * 60; // Stunden
            case "d" -> value * 24 * 60 * 60; // Tage
            case "w" -> value * 7 * 24 * 60 * 60; // Wochen
            default -> value; // Kein Suffix = Sekunden
        };
    }

    public static String decodeTime(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds cannot be negative");
        }

        if (seconds % (7 * 24 * 60 * 60) == 0) {
            return (seconds / (7 * 24 * 60 * 60)) + "w"; // Wochen
        } else if (seconds % (24 * 60 * 60) == 0) {
            return (seconds / (24 * 60 * 60)) + "d"; // Tage
        } else if (seconds % (60 * 60) == 0) {
            return (seconds / (60 * 60)) + "h"; // Stunden
        } else if (seconds % 60 == 0) {
            return (seconds / 60) + "m"; // Minuten
        } else {
            return seconds + "s"; // Sekunden
        }
    }

    public static String formatSekTime(int millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Time in sek cannot be negative");
        }

        // Konvertiere Millisekunden in passende Einheiten
        long weeks = millis / (7 * 24 * 60 * 60);
        millis %= (7 * 24 * 60 * 60 );

        long days = millis / (24 * 60 * 60);
        millis %= (24 * 60 * 60);

        long hours = millis / (60 * 60);
        millis %= (60 * 60);

        long minutes = millis / (60);
        millis %= (60);

        long seconds = millis ;

        // Baue die Ausgabe auf
        StringBuilder result = new StringBuilder();
        if (weeks > 0) result.append(weeks).append("w ");
        if (days > 0) result.append(days).append("d ");
        if (hours > 0) result.append(hours).append("h ");
        if (minutes > 0) result.append(minutes).append("m ");
        if (seconds > 0) result.append(seconds).append("s ");

        // Rückgabe, Leerzeichen am Ende entfernen
        return result.toString().trim();
    }
}
