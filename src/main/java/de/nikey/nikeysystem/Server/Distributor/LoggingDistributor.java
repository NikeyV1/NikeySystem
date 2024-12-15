package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.Player.API.ChatAPI;
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static de.nikey.nikeysystem.Server.API.LoggingAPI.logConfig;

public class LoggingDistributor {
    public static void loggingManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("blocklog")) {
            if (args.length >= 7) {
                try {
                    double x = Double.parseDouble(args[4]);
                    double y = Double.parseDouble(args[5]);
                    double z = Double.parseDouble(args[6]);

                    int ix = Integer.parseInt(args[4]);
                    int iy = Integer.parseInt(args[5]);
                    int iz = Integer.parseInt(args[6]);

                    Location location = new Location(sender.getWorld(), x, y, z);
                    String logKey = location.getWorld().getName() + "," + ix + "," + iy + "," + iz;

                    if (!logConfig.contains(logKey)) {
                        sender.sendMessage(Component.text("No changes recorded for this block").color(TextColor.color(157, 230, 41)));
                    } else {
                        sender.sendMessage(Component.text("Block changes: ").color(TextColor.color(157, 230, 41)).decorate(TextDecoration.UNDERLINED));
                        showBlockLog(sender,location);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cError: Coordinates must be valid numbers");
                }
            }
        }else if (cmd.equalsIgnoreCase("clearBlockLog")) {
            if (args.length >= 7) {
                try {
                    double x = Double.parseDouble(args[4]);
                    double y = Double.parseDouble(args[5]);
                    double z = Double.parseDouble(args[6]);

                    int ix = Integer.parseInt(args[4]);
                    int iy = Integer.parseInt(args[5]);
                    int iz = Integer.parseInt(args[6]);

                    Location location = new Location(sender.getWorld(), x, y, z);
                    String logKey = location.getWorld().getName() + "," + ix + "," + iy + "," + iz;

                    if (!logConfig.contains(logKey)) {
                        sender.sendMessage(Component.text("Nothing changed. There where no recorded changes").color(TextColor.color(157, 230, 41)));
                    } else {
                        sender.sendMessage(Component.text("Block changes").color(TextColor.color(157, 230, 41))
                                .append(Component.text(" cleared").color(NamedTextColor.RED)));
                        logConfig.set(logKey,null);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cError: Coordinates must be valid numbers");
                }
            }
        }else if (cmd.equalsIgnoreCase("cleanup")) {
            String timeLimit = args[4];
            try {
                int cleaned = cleanupLogs(timeLimit);

                sender.sendMessage(Component.text("There were ")
                        .color(TextColor.color(157, 230, 41))
                        .append(Component.text(cleaned).color(NamedTextColor.WHITE))
                        .append(Component.text(" logs older than "))
                        .append(Component.text(timeLimit).color(NamedTextColor.GRAY)))
                ;
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text("Invalid time format: " + timeLimit)
                        .color(NamedTextColor.RED));
            } catch (Exception e) {
                sender.sendMessage(Component.text("An error occurred while cleaning up logs: ").color(NamedTextColor.RED)
                        .append(Component.text(e.getMessage()).color(NamedTextColor.WHITE)));
                e.printStackTrace();
            }
        }else if (cmd.equalsIgnoreCase("player")) {
            if (args.length >= 6) {
                String player = args[4];
                sender.sendMessage(Component.text(player + "'s ").color(NamedTextColor.WHITE)
                        .append(Component.text("block changes: ").color(TextColor.color(157, 230, 41))).decoration(TextDecoration.BOLD,true));

                if (args[5].equalsIgnoreCase("Time")) {
                    String timeRange = args.length > 6 ? args[6] : null;
                    displayPlayerChanges(sender, player, timeRange);
                }else if (args[5].equalsIgnoreCase("Action")) {
                    displayPlayerActions(sender,args[7]);
                }else if (args[5].equalsIgnoreCase("Last")){

                }
            }
        }
    }

    public static void displayPlayerActions(CommandSender sender, String action) {
        for (String key : LoggingAPI.logConfig.getKeys(false)) {
            List<String> logEntries = LoggingAPI.logConfig.getStringList(key);

            for (String entry : logEntries) {
                String[] parts = entry.split(" ");

                if ((parts[1].equalsIgnoreCase("put") || parts[1].equalsIgnoreCase("took"))
                        && parts[1].equalsIgnoreCase(action)) {
                    TextComponent msg = Component.text(parts[0]) // Spielername
                            .color(TextColor.color(100, 100, 255)) // Blau
                            .append(Component.text(" " + parts[1] + " ") // Aktion (put/took)
                                    .color(NamedTextColor.LIGHT_PURPLE)) // Pink
                            .append(Component.text(parts[2] + " ") // Anzahl
                                    .color(TextColor.color(255, 255, 100))) // Gelb
                            .append(Component.text(parts[3].replace("_", " ")) // Item
                                    .color(TextColor.color(255, 100, 100))) // Rot
                            .append(Component.text(" " + parts[4] + " ") // in/from
                                    .color(TextColor.color(157, 230, 41))) // Grün
                            .append(Component.text(parts[5]) // Inventartyp
                                    .color(TextColor.color(100, 200, 255))) // Hellblau
                            .append(Component.text(" inventory ")
                                    .color(TextColor.color(157, 230, 41))) // Grün
                            .append(Component.text(parts[7] + " " + parts[8]) // Datum/Zeit
                                    .color(TextColor.color(255, 255, 100)));

                    sender.sendMessage(msg);
                    continue;
                }

                if (parts[0].equalsIgnoreCase("Block") && parts[3].equalsIgnoreCase(action)) {
                    Component message = Component.text("Block ")
                            .color(TextColor.color(157, 230, 41))
                            .append(Component.text(parts[1]) // Blockname
                                    .color(TextColor.color(255, 100, 100)))
                            .append(Component.text(" was ")
                                    .color(TextColor.color(157, 230, 41)))
                            .append(Component.text(parts[3].replace("_", " ")) // Action
                                    .color(NamedTextColor.DARK_PURPLE))
                            .append(Component.text(" by ")
                                    .color(TextColor.color(157, 230, 41)))
                            .append(Component.text(parts[5]) // Player
                                    .color(TextColor.color(100, 100, 255)))
                            .append(Component.text(" at ")
                                    .color(TextColor.color(157, 230, 41)))
                            .append(Component.text(parts[7]) // Date/Time
                                    .color(TextColor.color(255, 255, 100)));

                    sender.sendMessage(message);
                }
            }
        }
    }

    public static void displayPlayerChanges(CommandSender sender, String playerName, String timeRange) {
        if (timeRange == null) {
            for (String key : LoggingAPI.logConfig.getKeys(false)) {
                List<String> logEntries = LoggingAPI.logConfig.getStringList(key);

                for (String entry : logEntries) {
                    String[] parts = entry.split(" ");

                    if ((parts[1].equalsIgnoreCase("put") || parts[1].equalsIgnoreCase("took"))
                            && parts[0].equalsIgnoreCase(playerName)) {
                        TextComponent msg = Component.text(parts[0]) // Spielername
                                .color(TextColor.color(100, 100, 255)) // Blau
                                .append(Component.text(" " + parts[1] + " ") // Aktion (put/took)
                                        .color(NamedTextColor.LIGHT_PURPLE)) // Pink
                                .append(Component.text(parts[2] + " ") // Anzahl
                                        .color(TextColor.color(255, 255, 100))) // Gelb
                                .append(Component.text(parts[3].replace("_", " ")) // Item
                                        .color(TextColor.color(255, 100, 100))) // Rot
                                .append(Component.text(" " + parts[4] + " ") // in/from
                                        .color(TextColor.color(157, 230, 41))) // Grün
                                .append(Component.text(parts[5]) // Inventartyp
                                        .color(TextColor.color(100, 200, 255))) // Hellblau
                                .append(Component.text(" inventory ")
                                        .color(TextColor.color(157, 230, 41))) // Grün
                                .append(Component.text(parts[7] + " " + parts[8]) // Datum/Zeit
                                        .color(TextColor.color(255, 255, 100)));

                        sender.sendMessage(msg);
                        continue;
                    }

                    if (parts[0].equalsIgnoreCase("Block") && parts[5].equalsIgnoreCase(playerName)) {
                        Component message = Component.text("Block ")
                                .color(TextColor.color(157, 230, 41))
                                .append(Component.text(parts[1]) // Blockname
                                        .color(TextColor.color(255, 100, 100)))
                                .append(Component.text(" was ")
                                        .color(TextColor.color(157, 230, 41)))
                                .append(Component.text(parts[3].replace("_", " ")) // Action
                                        .color(NamedTextColor.DARK_PURPLE))
                                .append(Component.text(" by ")
                                        .color(TextColor.color(157, 230, 41)))
                                .append(Component.text(parts[5]) // Player
                                        .color(TextColor.color(100, 100, 255)))
                                .append(Component.text(" at ")
                                        .color(TextColor.color(157, 230, 41)))
                                .append(Component.text(parts[7]) // Date/Time
                                        .color(TextColor.color(255, 255, 100)));

                        sender.sendMessage(message);
                    }
                }
            }
            return;
        }
        long l = parseTime(timeRange);

        if (l == 0){
            sender.sendMessage(Component.text("Wrong usage").color(NamedTextColor.RED));
            return;
        }

        for (String key : LoggingAPI.logConfig.getKeys(false)) {
            List<String> logEntries = LoggingAPI.logConfig.getStringList(key);

            for (String entry : logEntries) {
                String[] parts = entry.split(" ");

                String[] at = entry.split(" at ");
                String datePart = at[1];

                if (isDateOlderThan(datePart, l)) continue;

                if ((parts[1].equalsIgnoreCase("put") || parts[1].equalsIgnoreCase("took"))
                        && parts[0].equalsIgnoreCase(playerName)) {
                    TextComponent msg = Component.text(parts[0]) // Spielername
                            .color(TextColor.color(100, 100, 255)) // Blau
                            .append(Component.text(" " + parts[1] + " ") // Aktion (put/took)
                                    .color(NamedTextColor.LIGHT_PURPLE)) // Pink
                            .append(Component.text(parts[2] + " ") // Anzahl
                                    .color(TextColor.color(255, 255, 100))) // Gelb
                            .append(Component.text(parts[3].replace("_", " ")) // Item
                                    .color(TextColor.color(255, 100, 100))) // Rot
                            .append(Component.text(" " + parts[4] + " ") // in/from
                                    .color(TextColor.color(157, 230, 41))) // Grün
                            .append(Component.text(parts[5]) // Inventartyp
                                    .color(TextColor.color(100, 200, 255))) // Hellblau
                            .append(Component.text(" inventory ")
                                    .color(TextColor.color(157, 230, 41))) // Grün
                            .append(Component.text(parts[7] + " " + parts[8]) // Datum/Zeit
                                    .color(TextColor.color(255, 255, 100)));

                    sender.sendMessage(msg);
                    continue;
                }

                if (parts[0].equalsIgnoreCase("Block") && parts[5].equalsIgnoreCase(playerName)) {
                    Component message = Component.text("Block ")
                            .color(TextColor.color(157, 230, 41))
                            .append(Component.text(parts[1]) // Blockname
                                    .color(TextColor.color(255, 100, 100)))
                            .append(Component.text(" was ")
                                    .color(TextColor.color(157, 230, 41)))
                            .append(Component.text(parts[3].replace("_", " ")) // Action
                                    .color(NamedTextColor.DARK_PURPLE))
                            .append(Component.text(" by ")
                                    .color(TextColor.color(157, 230, 41)))
                            .append(Component.text(parts[5]) // Player
                                    .color(TextColor.color(100, 100, 255)))
                            .append(Component.text(" at ")
                                    .color(TextColor.color(157, 230, 41)))
                            .append(Component.text(parts[7]) // Date/Time
                                    .color(TextColor.color(255, 255, 100)));

                    sender.sendMessage(message);
                }
            }
        }
    }





    private static int cleanupLogs(String ageLimit) {
        long timeInSeconds = parseTime(ageLimit);

        int deleted = 0;
        for (String key : LoggingAPI.logConfig.getKeys(false)) {
            List<String> logEntries = LoggingAPI.logConfig.getStringList(key);
            List<String> updatedEntries = new ArrayList<>();

            for (String entry : logEntries) {
                String[] parts = entry.split(" at ");
                String datePart = parts[1];

                if (!isDateOlderThan(datePart, timeInSeconds)) {
                    updatedEntries.add(entry);
                }else {
                    deleted+=1;
                }
            }

            if (updatedEntries.isEmpty()) {
                LoggingAPI.logConfig.set(key, null);
            } else {
                LoggingAPI.logConfig.set(key, updatedEntries);
            }
        }
        return deleted;
    }

    public static boolean isDateOlderThan(String dateString, long seconds) {
        try {
            int currentYear = new GregorianCalendar().get(Calendar.YEAR);

            String formattedDateString = currentYear + "-" + dateString;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd.MM-HH:mm");
            Date inputDate = sdf.parse(formattedDateString); // Datum parsen

            Date currentDate = new Date();

            long diffInMillis = currentDate.getTime() - inputDate.getTime();

            return diffInMillis > seconds * 1000;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Fällt auf false, falls etwas schief geht
    }

    private static long parseTime(String input) {
        if (input.endsWith("w")) {
            return Long.parseLong(input.replace("w", "")) * 7 * 24 * 60 * 60;
        } else if (input.endsWith("d")) {
            return Long.parseLong(input.replace("d", "")) * 24 * 60 * 60;
        } else if (input.endsWith("h")) {
            return Long.parseLong(input.replace("h", "")) * 60 * 60;
        } else if (input.endsWith("m")) {
            return Long.parseLong(input.replace("m", "")) * 60;
        } else if (input.endsWith("s")) {
            return Long.parseLong(input.replace("s", ""));
        }
        return 0;
    }



    private static void showBlockLog(CommandSender sender, Location location) {
        List<String> logValues = getLogValue(location);

        if (logValues.isEmpty()) {
            sender.sendMessage(Component.text("No changes recorded for this block")
                    .color(TextColor.color(157, 230, 41)));
            return;
        }

        for (String logValue : logValues) {
            String[] parts = logValue.split(" ");

            if (parts[1].equalsIgnoreCase("put") || parts[1].equalsIgnoreCase("took")) {
                TextComponent msg = Component.text(parts[0]) // Spielername
                        .color(TextColor.color(100, 100, 255)) // Blau
                        .append(Component.text(" " + parts[1] + " ") // Aktion (put/took)
                                .color(NamedTextColor.LIGHT_PURPLE)) // Pink
                        .append(Component.text(parts[2] + " ") // Anzahl
                                .color(TextColor.color(255, 255, 100))) // Gelb
                        .append(Component.text(parts[3].replace("_", " ")) // Item
                                .color(TextColor.color(255, 100, 100))) // Rot
                        .append(Component.text(" " + parts[4] + " ") // in/from
                                .color(TextColor.color(157, 230, 41))) // Grün
                        .append(Component.text(parts[5]) // Inventartyp
                                .color(TextColor.color(100, 200, 255))) // Hellblau
                        .append(Component.text(" inventory ")
                                .color(TextColor.color(157, 230, 41))) // Grün
                        .append(Component.text(parts[7] + " " + parts[8]) // Datum/Zeit
                                .color(TextColor.color(255, 255, 100)));

                sender.sendMessage(msg);
                continue;
            }
            Component message = Component.text("Block ")
                    .color(TextColor.color(157, 230, 41))
                    .append(Component.text(parts[1])  // Blockname
                            .color(TextColor.color(255, 100, 100)))
                    .append(Component.text(" was ")
                            .color(TextColor.color(157, 230, 41)))
                    .append(Component.text(parts[3].replace("_"," "))  // Action
                            .color(NamedTextColor.DARK_PURPLE))
                    .append(Component.text(" by ")
                            .color(TextColor.color(157, 230, 41)))
                    .append(Component.text(parts[5])  // Player
                            .color(TextColor.color(100, 100, 255)))
                    .append(Component.text(" at ")
                            .color(TextColor.color(157, 230, 41)))
                    .append(Component.text(parts[7])  // Date/Time
                            .color(TextColor.color(255, 255, 100)));

            sender.sendMessage(message);
        }
    }

    private static List<String> getLogValue(Location location) {
        String logKey = location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();

        return logConfig.getStringList(logKey);
    }
}
