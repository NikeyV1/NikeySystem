package de.nikey.nikeysystem.Server.Distributor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
                        sender.sendMessage(Component.text("Block changes: ").color(TextColor.color(157, 230, 41)));
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
        }
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

            Component message = Component.text("Block ")
                    .color(TextColor.color(157, 230, 41))
                    .append(Component.text(parts[1])  // Blockname
                            .color(TextColor.color(255, 100, 100)))
                    .append(Component.text(" was ")
                            .color(TextColor.color(157, 230, 41)))
                    .append(Component.text(parts[3].replace("_"," "))  // Action
                            .color(TextColor.color(235, 42, 165)))
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
