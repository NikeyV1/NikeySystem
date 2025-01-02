package de.nikey.nikeysystem.Player.API;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModerationAPI {

    private static final Set<UUID> frozenPlayers = new HashSet<>();

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


    public enum Reason {
        CHEATING("Cheating or using unfair advantages"),
        SPAMMING("Spamming in chat"),
        TOXICITY("Being toxic or disrespectful"),
        GRIEFING("Destroying or modifying other players builds"),
        HACKING("Using prohibited client modifications"),
        INAPPROPRIATE_CONTENT("Sharing inappropriate or offensive content"),
        RULE_BREAKING("Breaking a rule"),
        DEFAULT("No reason given"),
        OTHER("Other reason");

        private final String description;

        Reason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
