package de.nikey.nikeysystem.Player.API;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ModerationAPI {

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
