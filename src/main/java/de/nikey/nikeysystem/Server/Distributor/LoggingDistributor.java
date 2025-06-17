package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import de.nikey.nikeysystem.Server.Settings.LoggingSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

import static de.nikey.nikeysystem.Server.API.LoggingAPI.logConfig;

public class LoggingDistributor {
    public static void loggingManager(Player sender, String[] args) {
        String basePerm = "system.server.logging.";
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("blocklog")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "blocklog") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
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
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "clearblocklog") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
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
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "cleanup") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
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
        }else if (cmd.equalsIgnoreCase("filter")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "filter") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length >= 7) {
                String player = args[4];
                sender.sendMessage(Component.text("Block changes for filter:").color(TextColor.color(157, 230, 41)).decoration(TextDecoration.BOLD,true));

                String amount = args[5];
                String timestamp = args[6];

                int sec;
                try {
                    sec = MuteAPI.parseTime(args[7]);
                } catch (IllegalArgumentException e) {
                    sec = 0;
                }

                List<String> filteredLogs;

                if (args.length == 8) {
                    filteredLogs = filterLogs(player, amount, timestamp, sec,"");
                }else {
                    filteredLogs = filterLogs(player, amount, timestamp, sec,args[8]);
                }


                if (filteredLogs.isEmpty()) {
                    sender.sendMessage("No logs found with the given filters.");
                } else {
                    for (String log : filteredLogs) {
                        String[] parts = log.split(": ", 2);

                        if (parts.length == 2) {
                            String logKey = parts[0];
                            String logValue = parts[1];

                            String[] keyParts = logKey.split(",");
                            Component locationMessage = Component.text(keyParts[0] + " ")
                                    .color(TextColor.color(50, 162, 168))
                                    .append(Component.text(keyParts[1] + " ")
                                            .color(TextColor.color(30, 143, 109)))
                                    .append(Component.text(keyParts[2] + " ")
                                            .color(TextColor.color(30, 143, 109)))
                                    .append(Component.text(keyParts[3] + ": ")
                                            .color(TextColor.color(30, 143, 109)))
                                    .decoration(TextDecoration.ITALIC,true);

                            String[] logValueParts = logValue.split(" ");
                            Component valueMessage;

                            if (logValueParts[1].equalsIgnoreCase("put") || logValueParts[1].equalsIgnoreCase("took")) {
                                valueMessage = Component.text(logValueParts[0])
                                        .color(TextColor.color(100, 100, 255))
                                        .append(Component.text(" " + logValueParts[1] + " ")
                                                .color(NamedTextColor.LIGHT_PURPLE))
                                        .append(Component.text(logValueParts[2] + " ")
                                                .color(TextColor.color(255, 255, 100)))
                                        .append(Component.text(logValueParts[3].replace("_", " "))
                                                .color(TextColor.color(255, 100, 100)))
                                        .append(Component.text(" " + logValueParts[4] + " ")
                                                .color(TextColor.color(157, 230, 41)))
                                        .append(Component.text(logValueParts[5])
                                                .color(TextColor.color(100, 200, 255)))
                                        .append(Component.text(" inventory ")
                                                .color(TextColor.color(157, 230, 41)))
                                        .append(Component.text(logValueParts[7] + " " + logValueParts[8])
                                                .color(TextColor.color(255, 255, 100)));
                            } else {
                                valueMessage = Component.text("Block ")
                                        .color(TextColor.color(157, 230, 41))
                                        .append(Component.text(logValueParts[1])
                                                .color(TextColor.color(255, 100, 100)))
                                        .append(Component.text(" was ")
                                                .color(TextColor.color(157, 230, 41)))
                                        .append(Component.text(logValueParts[3].replace("_", " "))
                                                .color(NamedTextColor.DARK_PURPLE))
                                        .append(Component.text(" by ")
                                                .color(TextColor.color(157, 230, 41)))
                                        .append(Component.text(logValueParts[5])
                                                .color(TextColor.color(100, 100, 255)))
                                        .append(Component.text(" at ")
                                                .color(TextColor.color(157, 230, 41)))
                                        .append(Component.text(logValueParts[7])
                                                .color(TextColor.color(255, 255, 100)));
                            }

                            Component fullMessage = locationMessage.append(valueMessage.decoration(TextDecoration.ITALIC,false));

                            sender.sendMessage(fullMessage);
                        } else {
                            sender.sendMessage(Component.text("Invalid log format: " + log)
                                    .color(NamedTextColor.GRAY)); // Fallback für unformatierte Logs
                        }
                    }
                }
            }
        }else if (cmd.equalsIgnoreCase("settings")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "settings") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            LoggingSettings.openSettingsMenu(sender);
        }
    }
    private static List<String> filterLogs(String target, String amount, String timestamp, int seconds, String actiontype) {
        List<String> filteredLogs = new ArrayList<>();

        int limit = Integer.MAX_VALUE;
        if (!amount.equalsIgnoreCase("infinity")) {
            try {
                limit = Integer.parseInt(amount);
            } catch (NumberFormatException e) {
                return filteredLogs;
            }
        }

        for (String key : logConfig.getKeys(false)) {
            for (String log : logConfig.getStringList(key)) {
                if (filteredLogs.size() >= limit) break;

                if (!target.equalsIgnoreCase("null")) {
                    if (!LoggingAPI.getPlayer(log).equalsIgnoreCase(target))continue;
                }

                if (!timestamp.equalsIgnoreCase("null")) {
                    if (!LoggingAPI.getDate(log).startsWith(timestamp))continue;
                }

                if (seconds != 0) {
                    if (isDateOlderThan(LoggingAPI.getDate(log), seconds))continue;
                }

                if (!actiontype.isEmpty()) {
                    if (!LoggingAPI.getAction(log).equalsIgnoreCase(actiontype))continue;
                }

                filteredLogs.add(key + ": " + log);
            }

            if (filteredLogs.size() >= limit) break;
        }

        return filteredLogs;
    }

    private static int cleanupLogs(String ageLimit) {
        long timeInSeconds = parseTime(ageLimit);

        int deleted = 0;
        for (String key : logConfig.getKeys(false)) {
            List<String> logEntries = logConfig.getStringList(key);
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
                logConfig.set(key, null);
            } else {
                logConfig.set(key, updatedEntries);
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
