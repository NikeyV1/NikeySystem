package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.DataBases.PunishmentDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModerationAPI {
    private static final HashMap<UUID, Long> frozenPlayers = new HashMap<>();

    public static void freezePlayer(UUID player) {
        frozenPlayers.put(player, 0L); // 0 = permanent
    }

    public static void freezePlayer(UUID player, long durationSeconds) {
        long endTime = System.currentTimeMillis() + (durationSeconds * 1000);
        frozenPlayers.put(player, endTime);
    }

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers.keySet();
    }

    public static void unfreezePlayer(UUID player) {
        frozenPlayers.remove(player);
    }

    public static boolean isFrozen(UUID player) {
        if (!frozenPlayers.containsKey(player)) return false;

        long endTime = frozenPlayers.get(player);
        if (endTime == 0) return true; // permanent

        if (System.currentTimeMillis() > endTime) {
            unfreezePlayer(player);
            return false;
        }
        return true;
    }

    public static long getRemainingFreezeTime(UUID player) {
        if (!frozenPlayers.containsKey(player)) return 0;

        long endTime = frozenPlayers.get(player);
        if (endTime == 0) return -1;

        long remaining = (endTime - System.currentTimeMillis()) / 1000;
        return Math.max(remaining, 0);
    }


    public static int parseTime(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Time input cannot be null or empty");
        }

        // RegEx: Zahlen gefolgt von einem optionalen Suffix (s, m, h, d, w, M)
        Pattern pattern = Pattern.compile("^(\\d+(\\.\\d+)?)([smhdwM]?)$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time format. Use a number followed by s, m, h, d, w, or M");
        }

        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(3);

        return switch (unit) {
            case "s" -> value; // Sekunden
            case "m" -> value * 60; // Minuten
            case "h" -> value * 60 * 60; // Stunden
            case "d" -> value * 24 * 60 * 60; // Tage
            case "w" -> value * 7 * 24 * 60 * 60; // Wochen
            case "M" -> value * 30 * 24 * 60 * 60; // Monate (Durchschnitt von 30 Tagen pro Monat)
            default -> value; // Kein Suffix = Sekunden
        };
    }

    public static String formatTime(int seconds) {
        if (seconds <= 0) return "0s";

        if (seconds >= 30 * 24 * 60 * 60) {
            double months = seconds / (30.0 * 24 * 60 * 60);
            return String.format(Locale.US,"%.1fM", months);
        }
        // Wochen
        else if (seconds >= 7 * 24 * 60 * 60) {
            double weeks = seconds / (7.0 * 24 * 60 * 60);
            return String.format(Locale.US,"%.1fw", weeks);
        }
        // Tage
        else if (seconds >= 24 * 60 * 60) {
            double days = seconds / (24.0 * 60 * 60);
            return String.format(Locale.US,"%.1fd", days);
        }
        // Stunden
        else if (seconds >= 60 * 60) {
            double hours = seconds / (60.0 * 60);
            return String.format(Locale.US,"%.1fh", hours);
        }
        // Minuten
        else if (seconds >= 60) {
            double minutes = seconds / 60.0;
            return String.format(Locale.US,"%.1fm", minutes);
        }
        // Sekunden
        else {
            return seconds + "s";
        }
    }


    public static class KickMessanges {
        public static Component createPermanentBanMessage(String reason) {
            return Component.text()
                    .append(Component.text("NikeySystem", NamedTextColor.RED))
                    .append(Component.text(" » ", NamedTextColor.WHITE))
                    .append(Component.text("You are permanently banned", NamedTextColor.RED))
                    .append(Component.newline())
                    .append(Component.space())
                    .append(Component.newline())
                    .append(Component.text("Reason ", NamedTextColor.RED))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(reason, NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.space())
                    .append(Component.newline())
                    .append(Component.text("Unban application in Discord", NamedTextColor.DARK_GRAY))
                    .build();
        }

        public static Component createTemporaryBanMessage(String reason, String expiry) {
            LocalDateTime expiryTime = LocalDateTime.parse(expiry, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(now, expiryTime);
            String remainingTime;

            if (duration.isNegative() || duration.isZero()) {
                remainingTime = "Ban expired"; // Falls der Ban bereits abgelaufen ist
            } else {
                long totalSeconds = duration.getSeconds();
                long months = totalSeconds / (30L * 24 * 60 * 60); // 30 Tage als grobe Annahme für einen Monat
                totalSeconds %= 30L * 24 * 60 * 60;

                long weeks = totalSeconds / (7L * 24 * 60 * 60);
                totalSeconds %= 7L * 24 * 60 * 60;

                long days = totalSeconds / (24 * 60 * 60);
                totalSeconds %= 24 * 60 * 60;

                long hours = totalSeconds / (60 * 60);
                totalSeconds %= 60 * 60;

                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                remainingTime = (months > 0 ? months + "mo " : "") +
                        (weeks > 0 ? weeks + "w " : "") +
                        (days > 0 ? days + "d " : "") +
                        (hours > 0 ? hours + "h " : "") +
                        (minutes > 0 ? minutes + "m " : "") +
                        (seconds > 0 ? seconds + "s" : "");
            }

            return Component.text()
                    .append(Component.text("NikeySystem", NamedTextColor.RED))
                    .append(Component.text(" » ", NamedTextColor.WHITE))
                    .append(Component.text("You are temporarily banned", NamedTextColor.RED))
                    .append(Component.newline())
                    .append(Component.space())
                    .append(Component.newline())
                    .append(Component.text("Reason ", NamedTextColor.RED))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(reason, NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.space())
                    .append(Component.newline())
                    .append(Component.text("Ban removed in ", NamedTextColor.RED))
                    .append(Component.text("» ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(remainingTime, NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.space())
                    .append(Component.newline())
                    .append(Component.text("Unban application in Discord", NamedTextColor.DARK_GRAY))
                    .build();
        }
    }

    public static void punishmentStartup() {
        PunishmentDatabase.connect();
        PunishmentDatabase.loadAllData();
    }

    public static void punishmentShutdown() {
        PunishmentDatabase.saveAllData();
        PunishmentDatabase.disconnect();
    }
}