package de.nikey.nikeysystem.Server.API;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackupAPI {

    public static long parseTime(String input) throws IllegalArgumentException {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Time input cannot be null or empty");
        }

        // RegEx: Zahlen gefolgt von einem optionalen Suffix (m, h, d, w)
        Pattern pattern = Pattern.compile("^(\\d+)([smhdw]?)$");
        Matcher matcher = pattern.matcher(input.toLowerCase());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time format. Use a number followed by s, m, h, d, or w");
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "s" -> // Sekunden
                    value * 1000;
            case "m" -> // Minuten
                    value * 60 * 1000;
            case "h" -> // Stunden
                    value * 60 * 60 * 1000;
            case "d" -> // Tage
                    value * 24 * 60 * 60 * 1000;
            case "w" -> // Wochen
                    value * 7 * 24 * 60 * 60 * 1000;
            default -> // Kein Suffix = Sekunden
                    value * 1000;
        };
    }
}
