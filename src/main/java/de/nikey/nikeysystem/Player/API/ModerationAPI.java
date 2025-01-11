package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModerationAPI {

    private static final Set<UUID> frozenPlayers = new HashSet<>();

    public static Set<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }

    public static boolean isFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    public static void freezePlayer(UUID uuid) {
        frozenPlayers.add(uuid);
    }

    public static void unfreezePlayer(UUID uuid) {
        frozenPlayers.remove(uuid);
    }

    public static int parseTime(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Time input cannot be null or empty");
        }

        // RegEx: Zahlen gefolgt von einem optionalen Suffix (s, m, h, d, w, M)
        Pattern pattern = Pattern.compile("^(\\d+)([smhdwM]?)$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time format. Use a number followed by s, m, h, d, w, or M");
        }

        int value = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);

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

    public static Duration parseDuration(String input) {
        Map<Character, Long> timeUnits = new HashMap<>();
        timeUnits.put('M', 30L * 24 * 60 * 60); // Months (30 days)
        timeUnits.put('w', 7L * 24 * 60 * 60);  // Weeks
        timeUnits.put('d', 24L * 60 * 60);      // Days
        timeUnits.put('h', 60L * 60);           // Hours
        timeUnits.put('m', 60L);                // Minutes

        try {
            char unit = input.charAt(input.length() - 1);
            long value = Long.parseLong(input.substring(0, input.length() - 1));
            long seconds = timeUnits.getOrDefault(unit, 0L) * value;
            return Duration.ofSeconds(seconds);
        } catch (Exception e) {
            return null;
        }
    }


    public static class KickMessanges {

        /**
         * Erstellt eine Nachricht für einen permanenten Bann.
         *
         * @param reason Der Bann-Grund
         * @return Die Adventure-Component für den Bann-Bildschirm
         */
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

        /**
         * Erstellt eine Nachricht für einen temporären Bann.
         *
         * @param reason Der Bann-Grund
         * @param expiry Das Ablaufdatum des Banns
         * @return Die Adventure-Component für den Bann-Bildschirm
         */
        public static Component createTemporaryBanMessage(String reason, String expiry) {

            LocalDateTime expiryTime = LocalDateTime.parse(expiry, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime now = LocalDateTime.now();

            // Differenz berechnen
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

                // Verbleibende Zeit in lesbarem Format
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
}
